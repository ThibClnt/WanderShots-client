package fr.efrei.wandershots.client.ui.walking;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;

import fr.efrei.wandershots.client.data.CredentialsManager;
import fr.efrei.wandershots.client.entities.Picture;
import fr.efrei.wandershots.client.entities.User;
import fr.efrei.wandershots.client.entities.Walk;
import fr.efrei.wandershots.client.repositories.PictureRepository;
import fr.efrei.wandershots.client.repositories.WalkRepository;
import fr.efrei.wandershots.client.utils.LocationUtils;
import fr.efrei.wandershots.client.utils.TimeUtils;

public class WalkingViewModel extends ViewModel {

    private final MutableLiveData<PolylineOptions> polylineOptionsLiveData = new MutableLiveData<>();
    public LiveData<PolylineOptions> getPolylineOptions() { return polylineOptionsLiveData; }

    private final MutableLiveData<Location> lastLocationLiveData = new MutableLiveData<>();
    public LiveData<Location> getLastLocation() { return lastLocationLiveData; }

    private final MutableLiveData<Double> totalDistanceLiveData = new MutableLiveData<>(0.0);
    public LiveData<Double> getTotalDistance() { return totalDistanceLiveData; }

    private final MutableLiveData<Long> elapsedTimeLiveData = new MutableLiveData<>(0L);
    public LiveData<Long> getElapsedTime() { return elapsedTimeLiveData; }

    private final MutableLiveData<Double> speedLiveData = new MutableLiveData<>(0.0);
    public LiveData<Double> getSpeed() { return speedLiveData; }

    private final MutableLiveData<String> titleLiveData = new MutableLiveData<>("");
    public LiveData<String> getTitle() { return titleLiveData; }

    private final MutableLiveData<Date> startTimeLiveData = new MutableLiveData<>(new Date());
    public LiveData<Date> getStartTime() { return startTimeLiveData; }

    private final MutableLiveData<ArrayList<Picture>> picturesLiveData = new MutableLiveData<>(new ArrayList<>());
    public LiveData<ArrayList<Picture>> getPictures() { return picturesLiveData; }

    private final WalkRepository walkRepository;
    private final PictureRepository pictureRepository;

    private double deltaDistance;
    private double elapsedTimeBetweenLocations;

    public WalkingViewModel() {
        walkRepository = WalkRepository.getInstance();
        pictureRepository = PictureRepository.getInstance();
        PolylineOptions polylineOptions = new PolylineOptions()
                .color(Color.BLUE);
        polylineOptionsLiveData.setValue(polylineOptions);

    }

    public void updateLocation(Location location) {
        Location lastLocation = lastLocationLiveData.getValue();
        lastLocationLiveData.setValue(location);

        if (lastLocation != null) {
            Log.d("WalkingViewModel", "Update location: " + lastLocation + " -> " + location);
            deltaDistance = lastLocation.distanceTo(location);   // in meters
            elapsedTimeBetweenLocations = (location.getTime() - lastLocation.getTime()) / 1000f; // in seconds

            Double totalDistance = totalDistanceLiveData.getValue();
            totalDistance += deltaDistance;
            totalDistanceLiveData.setValue(totalDistance);
        }

        lastLocationLiveData.setValue(location);

        if (polylineOptionsLiveData.getValue() != null) {
            PolylineOptions polylineOptions = polylineOptionsLiveData.getValue();
            polylineOptions.add(LocationUtils.getPosition(location));
            polylineOptionsLiveData.setValue(polylineOptions);
        }
    }

    public void updateTime() {
        if (startTimeLiveData.getValue() == null)
            return;

        long elapsedTime = TimeUtils.getElapsedTime(startTimeLiveData.getValue());
        elapsedTimeLiveData.setValue(elapsedTime);

        double speed = (deltaDistance > 0 ? deltaDistance / elapsedTimeBetweenLocations : 0) * 3.6; // in km/h
        speedLiveData.setValue(speed);
    }

    public void setTitle(String title) {
        titleLiveData.setValue(title);
    }

    public void addPicture(Picture picture) {
        if (lastLocationLiveData.getValue() != null) {
            Location location = lastLocationLiveData.getValue();
            picture.setLatitude(location.getLatitude());
            picture.setLongitude(location.getLongitude());
        }

        ArrayList<Picture> pictures = picturesLiveData.getValue() != null ? picturesLiveData.getValue() : new ArrayList<>();
        pictures.add(picture);
        picturesLiveData.setValue(pictures);
    }

    public void stopWalk(Context context) {
        if (startTimeLiveData.getValue() == null || totalDistanceLiveData.getValue() == null)
            return;

        long elapsedTime = TimeUtils.getElapsedTime(startTimeLiveData.getValue());

        String title = titleLiveData.getValue();
        if (title == null || title.isEmpty()) {
            title = "Walk " + TimeUtils.formatDateTime(startTimeLiveData.getValue());
        }

        User user = CredentialsManager.getInstance(context).getCredentialsFromCache();

        Walk walk = new Walk();
        walk.setTitle(title);
        walk.setStartTime(startTimeLiveData.getValue());
        walk.setDuration(elapsedTime);
        walk.setDistance(totalDistanceLiveData.getValue());

        if (user != null)
            walk.setUserId(user.getUserId());

        int walkId = walkRepository.saveWalk(walk);

        if (picturesLiveData.getValue() != null) {
            for (Picture picture : picturesLiveData.getValue()) {
                picture.setWalkId(walkId);
                pictureRepository.savePicture(picture);
            }
        }
    }

    public void reset() {
        polylineOptionsLiveData.setValue(new PolylineOptions().color(Color.BLUE));
        lastLocationLiveData.setValue(null);
        totalDistanceLiveData.setValue(0.0);
        elapsedTimeLiveData.setValue(0L);
        speedLiveData.setValue(0.0);
        titleLiveData.setValue("");
        startTimeLiveData.setValue(new Date());
        picturesLiveData.setValue(new ArrayList<>());
    }
}
