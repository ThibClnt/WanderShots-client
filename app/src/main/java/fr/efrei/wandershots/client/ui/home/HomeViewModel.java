package fr.efrei.wandershots.client.ui.home;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.efrei.wandershots.client.data.CredentialsManager;
import fr.efrei.wandershots.client.entities.Picture;
import fr.efrei.wandershots.client.entities.User;
import fr.efrei.wandershots.client.repositories.PictureRepository;
import fr.efrei.wandershots.client.repositories.UserRepository;

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<Double> totalDistanceLiveData = new MutableLiveData<>(0.0);
    public LiveData<Double> getTotalDistance() { return totalDistanceLiveData; }

    private final MutableLiveData<List<Picture>> picturesLiveData = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Picture>> getPictures() { return picturesLiveData; }

    public HomeViewModel(Application application) throws IllegalStateException {
        super(application);
        UserRepository userRepository = UserRepository.getInstance();
        PictureRepository pictureRepository = PictureRepository.getInstance();

        User user = CredentialsManager.getInstance(application).getCredentialsFromCache();

        if (user == null)
            throw new IllegalStateException("User must be logged in to access home view model");

        new Thread(() -> {
            double totalDistance = 0;
            try {
                totalDistance = userRepository.getUserDistance(user.getUserId());
            } catch (SQLException e) {
                Log.e(this.getClass().getName(), "Failed to get user distance", e);
            }
            totalDistanceLiveData.postValue(totalDistance);
            picturesLiveData.postValue(pictureRepository.getAllUserPictures(user.getUserId()));
        }).start();
    }
}