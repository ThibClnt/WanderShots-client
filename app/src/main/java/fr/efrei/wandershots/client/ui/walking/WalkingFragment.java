package fr.efrei.wandershots.client.ui.walking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.efrei.wandershots.client.MainActivity;
import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentWalkingBinding;
import fr.efrei.wandershots.client.entities.Picture;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.picture.PictureFragment;
import fr.efrei.wandershots.client.ui.tabs.TabbedFragment;

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
        // Setup the map
        MapView mMapView = binding.mapView;
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        // Instantiate PolylineOptions
        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(10);

        // Restore state
        if (savedInstanceState != null) {
            pictures = (List<Picture>) savedInstanceState.getSerializable(PICTURES_LIST_KEY);
            polylineOptions = savedInstanceState.getParcelable(POLYLINE_OPTIONS_KEY);
            startTime = (Date) savedInstanceState.getSerializable(START_TIME_KEY);
            elapsedTimeBetweenLocations = savedInstanceState.getDouble(ELAPSED_TIME_BETWEEN_LOCATIONS_KEY);
            deltaDistance = savedInstanceState.getDouble(DELTA_DISTANCE_KEY);
            totalDistance = savedInstanceState.getDouble(TOTAL_DISTANCE_KEY);
            lastLocation = savedInstanceState.getParcelable(LAST_LOCATION_KEY);
            title = savedInstanceState.getString(TITLE_TAG);

            handler.post(() -> binding.walkNameInput.setText(title));
        } else {
            startTime = new Date();
        }

        // Setup the stop walk button
        binding.stopWalk.setOnClickListener(v -> onStopWalk());

        // Setup the take picture button
        binding.takePicture.setOnClickListener(v -> navigateToFragment(PictureFragment.newInstance()));
        // set up start time
        handler.post(()->binding.startTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTime)));

        handler.post(updateUIRunnable);
    }

    //region Map lifecycle
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setBuildingsEnabled(true);

        // Request location permissions if necessary
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            displayMyCurrentLocation();
        }
    }

    @SuppressLint("MissingPermission")
    public void displayMyCurrentLocation() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        // Retrieve the last known location
        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        handler.post(this::updateMap);

        LocationListener locationListener = location -> {

            if (lastLocation != null) {
                deltaDistance = lastLocation.distanceTo(location);
                totalDistance += deltaDistance;
                elapsedTimeBetweenLocations = (location.getTime() - lastLocation.getTime()) / 1000f;
            }

            lastLocation = location;
            handler.post(this::updateMap);
        };

        // Register the location listener with the location manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }

    private void updateMap(){
        map.clear();
        updateMarker();
        drawRoute();
    }

    private void updateMarker() {
        LatLng lastPoint = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        map.addMarker(new MarkerOptions().position(lastPoint).title(requireActivity().getString(R.string.position_marker)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPoint, 15f));
    }

    private void drawRoute() {
        polylineOptions.add(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
        Polyline polyline = map.addPolyline(polylineOptions);
    }

    private String getFormattedTime(long msTime) {
        long hours = (msTime / 1000) / 3600;
        long minutes = ((msTime / 1000) % 3600) / 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
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
        long elapsedTime = System.currentTimeMillis() - startTime.getTime();
        binding.duration.setText(getFormattedTime(elapsedTime));

        // Calculate and Update Speed
        double speed = (deltaDistance > 0 ? deltaDistance / elapsedTimeBetweenLocations : 0) / 3.6; // km/h
        binding.speed.setText(String.format(Locale.getDefault(), "%.1f km/h", speed));
    }

    public void onStopWalk(){
        // 1 - Save (todo)

        // 2 - Navigate to the home fragment
        navigateToFragment(TabbedFragment.newInstance(), false);
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