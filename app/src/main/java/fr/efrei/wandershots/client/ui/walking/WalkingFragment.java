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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Polyline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.efrei.wandershots.client.MainActivity;
import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentWalkingBinding;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.picture.PictureFragment;
import fr.efrei.wandershots.client.ui.tabs.TabbedFragment;

public class WalkingFragment extends WandershotsFragment<FragmentWalkingBinding> implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location lastLocation;
    private float totalDistance = 0;
    private long startTime;
    private float elapsedTimeBetweenLocations = 0; // in s
    private float deltaDistance = 0; // in m
    private List<LatLng> pointsList = new ArrayList<>();


    public static WalkingFragment newInstance() {
        return new WalkingFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Setup the map
        MapView mMapView = binding.mapView;
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        // Setup the stop walk button
        binding.stopWalk.setOnClickListener(v -> onStopWalk());

        // Setup the take picture button
        binding.takePicture.setOnClickListener(v -> navigateToPictureFragment());
        // set up start time
        handler.post(()->binding.startTime.setText(getCurrentTime()));

    }

    //region Map lifecycle
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);


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
        if (mMap == null)
            return;

        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager == null)
            return;

        // Retrieve the last known location
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLocation != null) {
            handler.post(()->updateMarker(lastKnownLocation));
        }

        LocationListener locationListener = location -> {

            if (lastLocation != null) {
                deltaDistance = lastLocation.distanceTo(location);
                totalDistance += deltaDistance;
                elapsedTimeBetweenLocations = (location.getTime() - lastLocation.getTime()) / 1000f;
                handler.post(()->updateUI(location));
            }
            lastLocation = location;
            LatLng newPoint = new LatLng(location.getLatitude(), location.getLongitude());
            pointsList.add(newPoint);
            handler.post(this::drawRoute);

        };

        // Register the location listener with the location manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);
    }

    private String getCurrentTime() {
        startTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
    private void updateMarker(Location location) {
        // When the location changes, update the marker and move the camera
        LatLng updatedUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
        // Clear previous markers
        mMap.clear();
        // Add a new marker
        mMap.addMarker(new MarkerOptions().position(updatedUserLocation).title(getString(R.string.position_marker)));
        // Move the camera to the updated user location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(updatedUserLocation, 15f));

    }
    private void drawRoute() {
        mMap.clear();

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(10);
        for (LatLng point : pointsList) {
            polylineOptions.add(point);
        }

        Polyline polyline = mMap.addPolyline(polylineOptions);

    }
    private void updateUI(Location location) {
        // Update Traveled Distance
        binding.traveledDistance.setText(String.format(Locale.getDefault(), "%.2f km", totalDistance / 1000));

        // Calculate and Update Duration

        long elapsedTime = System.currentTimeMillis() - startTime;
        long elapsedHours = (elapsedTime / 1000) / 3600;
        long elapsedMinutes = ((elapsedTime / 1000) % 3600) / 60;
        binding.duration.setText(String.format(Locale.getDefault(), "%02d:%02d", elapsedHours, elapsedMinutes));

        // Calculate and Update Pace
        float pace = deltaDistance > 0 ? deltaDistance / elapsedTimeBetweenLocations : 0; // m/s
        binding.pace.setText(String.format(Locale.getDefault(), "%.2f m/s", pace));
    }

    public void onStopWalk(){
        // 1 - Save (todo)

        // 2 - Navigate to the home fragment
        navigateToFragment(TabbedFragment.newInstance(), false);
    }
}