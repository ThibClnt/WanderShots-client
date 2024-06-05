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
import java.util.Date;
import java.util.Locale;

import fr.efrei.wandershots.client.MainActivity;
import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentWalkingBinding;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.picture.PictureFragment;
import fr.efrei.wandershots.client.ui.tabs.TabbedFragment;

public class WalkingFragment extends WandershotsFragment<FragmentWalkingBinding> implements OnMapReadyCallback {

    private GoogleMap map;
    private Location lastLocation;
    private float totalDistance = 0;
    private long startTime;
    private float elapsedTimeBetweenLocations = 0; // in s
    private float deltaDistance = 0; // in m

    private PolylineOptions polylineOptions;


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

        // Setup the stop walk button
        binding.stopWalk.setOnClickListener(v -> onStopWalk());

        // Setup the take picture button
        binding.takePicture.setOnClickListener(v -> navigateToFragment(PictureFragment.newInstance()));
        // set up start time
        startTime = System.currentTimeMillis();
        handler.post(()->binding.startTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date())));

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
        if (map == null)
            return;

        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager == null)
            return;

        // Retrieve the last known location
        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastLocation != null) {
            handler.post(this::updateMarker);
        }

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

    private void drawRoute() {
        polylineOptions.add(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
        Polyline polyline = map.addPolyline(polylineOptions);
    }

    private void updateMarker() {
        LatLng lastPoint = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        // Add a new marker
        map.addMarker(new MarkerOptions().position(lastPoint).title(getString(R.string.position_marker)));
        // Move the camera to the updated user location
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPoint, 15f));
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
        long elapsedTime = System.currentTimeMillis() - startTime;
        binding.duration.setText(getFormattedTime(elapsedTime));

        // Calculate and Update Speed
        double speed = (deltaDistance > 0 ? deltaDistance / elapsedTimeBetweenLocations : 0) / 3.6; // km/h
        binding.speed.setText(String.format(Locale.getDefault(), "%.2f km/h", speed));
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
}