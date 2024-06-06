package fr.efrei.wandershots.client.ui.walking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentWalkingBinding;
import fr.efrei.wandershots.client.entities.Picture;
import fr.efrei.wandershots.client.entities.Walk;
import fr.efrei.wandershots.client.repositories.WalkRepository;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.picture.PictureFragment;
import fr.efrei.wandershots.client.ui.tabs.TabbedFragment;
import fr.efrei.wandershots.client.utils.LocationUtils;
import fr.efrei.wandershots.client.utils.PermissionUtils;
import fr.efrei.wandershots.client.utils.TimeUtils;

public class WalkingFragment extends WandershotsFragment<FragmentWalkingBinding> implements OnMapReadyCallback {

    private static final String POLYLINE_OPTIONS_KEY = "polylineOptions";
    private static final String PICTURES_LIST_KEY = "picturesList";
    private static final String START_TIME_KEY = "startTime";
    private static final String ELAPSED_TIME_BETWEEN_LOCATIONS_KEY = "elapsedTimeBetweenLocations";
    private static final String DELTA_DISTANCE_KEY = "deltaDistance";
    private static final String TOTAL_DISTANCE_KEY = "totalDistance";
    private static final String LAST_LOCATION_KEY = "lastLocation";
    private static final String TITLE_TAG = "title";

    private GoogleMap map;
    private PolylineOptions polylineOptions;

    private Location lastLocation;
    private Date startTime;
    private double elapsedTimeBetweenLocations = 0; // in s
    private double deltaDistance = 0; // in m
    private double totalDistance = 0;

    private String title = "";

    private List<Picture> pictures = new ArrayList<>();

    public static WalkingFragment newInstance() {
        return new WalkingFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Restore state
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            startTime = new Date();

            polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.RED);
            polylineOptions.width(10);
        }

        // Setup the buttons
        binding.stopWalk.setOnClickListener(v -> onStopWalk());
        binding.takePicture.setOnClickListener(v -> navigateToFragment(PictureFragment.newInstance()));

        // Setup the map
        MapView mMapView = binding.mapView;
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        // Initialize the UI
        handler.post(()->binding.startTime.setText(TimeUtils.formatTime(startTime)));
        handler.post(updateUIRunnable);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        if (PermissionUtils.hasLocationPermission(requireContext())) {
            startLocationTracking();
        } else {
            PermissionUtils.requestLocationPermission(requireActivity(), this::startLocationTracking);
        }
    }

    @SuppressLint("MissingPermission")
    public void startLocationTracking() {
        map.setBuildingsEnabled(true);
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(() -> {
            centerOnLocation();
            return true;
        });

        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        // Set initial location
        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateMap();
        centerOnLocation();

        // Register the location listener with the location manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this::onUpdateLocation);
    }

    private void onUpdateLocation(Location location) {
        if (lastLocation == null)
            return;

        deltaDistance = lastLocation.distanceTo(location);
        totalDistance += deltaDistance;
        elapsedTimeBetweenLocations = (location.getTime() - lastLocation.getTime()) / 1000f;
        lastLocation = location;
        updateMap();
    }

    private void centerOnLocation() {
        handler.post(() -> {
            LatLng lastPoint = LocationUtils.getPosition(lastLocation);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPoint, 15));
        });
    }

    private void updateMap(){
        handler.post(() -> {
            map.clear();
            updateMarker();
            drawRoute();
        });
    }

    private void updateMarker() {
        LatLng lastPoint = LocationUtils.getPosition(lastLocation);

        map.addMarker(
                new MarkerOptions()
                        .position(lastPoint)
                        .title(requireActivity().getString(R.string.position_marker))
        );
    }

    private void drawRoute() {
        polylineOptions.add(LocationUtils.getPosition(lastLocation));
        Polyline polyline = map.addPolyline(polylineOptions);
    }

    private final Runnable updateUIRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            handler.postDelayed(this, 1000);
        }
    };

    private void updateUI() {
        // Update Traveled Distance
        binding.traveledDistance.setText(String.format(Locale.getDefault(), "%.2f km", totalDistance / 1000));

        // Calculate and Update Duration
        long elapsedTime = TimeUtils.getElapsedTime(startTime);
        binding.duration.setText(TimeUtils.formatTime(elapsedTime));

        // Calculate and Update Speed
        double speed = (deltaDistance > 0 ? deltaDistance / elapsedTimeBetweenLocations : 0) / 3.6; // km/h
        binding.speed.setText(String.format(Locale.getDefault(), "%.1f km/h", speed));
    }

    public void onStopWalk() {
        // 1 - Calculate the duration
        long elapsedTime = TimeUtils.getElapsedTime(startTime);

        // 2 - Get the walk title
        if (binding.walkNameInput.getText() != null) {
            title = binding.walkNameInput.getText().toString();
        }

        // 3 - Create a new Walk object with the current data
        Walk walk = new Walk();
        walk.setTitle(title);
        walk.setStartTime(startTime);
        walk.setDuration(elapsedTime);
        walk.setDistance(totalDistance);

        // 4 - Save the walk using WalkRepository
        WalkRepository walkRepository = WalkRepository.getInstance();
        walkRepository.saveWalk(walk);

        Toast.makeText(requireContext(), "you clicked on stop", Toast.LENGTH_SHORT).show();

        // 6 - Navigate to the home fragment
        navigateToFragment(TabbedFragment.newInstance(), false);
        Toast.makeText(requireContext(), "the end my friend", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onPause() {
        handler.removeCallbacksAndMessages(updateUIRunnable);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(updateUIRunnable);
    }

    public void restoreState(@NonNull Bundle savedInstanceState) {
        pictures = (List<Picture>) savedInstanceState.getSerializable(PICTURES_LIST_KEY);
        polylineOptions = savedInstanceState.getParcelable(POLYLINE_OPTIONS_KEY);
        startTime = (Date) savedInstanceState.getSerializable(START_TIME_KEY);
        elapsedTimeBetweenLocations = savedInstanceState.getDouble(ELAPSED_TIME_BETWEEN_LOCATIONS_KEY);
        deltaDistance = savedInstanceState.getDouble(DELTA_DISTANCE_KEY);
        totalDistance = savedInstanceState.getDouble(TOTAL_DISTANCE_KEY);
        lastLocation = savedInstanceState.getParcelable(LAST_LOCATION_KEY);
        title = savedInstanceState.getString(TITLE_TAG);

        handler.post(() -> binding.walkNameInput.setText(title));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (binding.walkNameInput.getText() != null)
            outState.putString(TITLE_TAG, binding.walkNameInput.getText().toString());

        outState.putSerializable(PICTURES_LIST_KEY, (ArrayList<Picture>) pictures);
        outState.putParcelable(POLYLINE_OPTIONS_KEY, polylineOptions);
        outState.putSerializable(START_TIME_KEY, startTime);
        outState.putDouble(ELAPSED_TIME_BETWEEN_LOCATIONS_KEY, elapsedTimeBetweenLocations);
        outState.putDouble(DELTA_DISTANCE_KEY, deltaDistance);
        outState.putDouble(TOTAL_DISTANCE_KEY, totalDistance);
        outState.putParcelable(LAST_LOCATION_KEY, lastLocation);
    }
}