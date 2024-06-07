package fr.efrei.wandershots.client.ui.walking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import fr.efrei.wandershots.client.databinding.FragmentWalkingBinding;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.picture.PictureFragment;
import fr.efrei.wandershots.client.ui.tabs.TabbedFragment;
import fr.efrei.wandershots.client.utils.LocationUtils;
import fr.efrei.wandershots.client.utils.PermissionUtils;
import fr.efrei.wandershots.client.utils.TimeUtils;

public class WalkingFragment extends WandershotsFragment<FragmentWalkingBinding> implements OnMapReadyCallback {

    private GoogleMap map;
    private MapView mapView;
    private WalkingViewModel viewModel;

    public static WalkingFragment newInstance() {
        return new WalkingFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(WalkingViewModel.class);

        // Setup the buttons
        binding.stopWalk.setOnClickListener(v -> onStopWalk());
        binding.takePicture.setOnClickListener(v -> navigateToFragment(PictureFragment.newInstance()));

        // Setup the mapView
        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Setup the viewModel observers
        viewModel.getStartTime().observe(getViewLifecycleOwner(), startTime ->
                handler.post(() -> binding.startTime.setText(TimeUtils.formatTime(startTime))));

        viewModel.getTotalDistance().observe(getViewLifecycleOwner(), distance ->
                handler.post(() -> binding.traveledDistance.setText(String.format(Locale.getDefault(), "%.2f km", distance / 1000))));

        viewModel.getElapsedTime().observe(getViewLifecycleOwner(), elapsedTime ->
                binding.duration.setText(TimeUtils.formatTime(elapsedTime)));

        viewModel.getSpeed().observe(getViewLifecycleOwner(), speed ->
                binding.speed.setText(String.format(Locale.getDefault(), "%.1f km/h", speed)));

        viewModel.getPolylineOptions().observe(getViewLifecycleOwner(), polylineOptions -> {
            if (map != null) {
                map.clear();
                map.addPolyline(polylineOptions);
            }
        });

        // Start the UI update loop
        handler.post(updateUIRunnable);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setBuildingsEnabled(true);

        if (PermissionUtils.hasLocationPermission(requireContext())) {
            startLocationTracking();
        } else {
            PermissionUtils.requestLocationPermission(requireActivity(), this::startLocationTracking);
        }
    }

    @SuppressLint("MissingPermission")
    public void startLocationTracking() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(() -> {

            centerOnLocation();
            debug("Centering on location");

            return true;
        });

        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation != null) {
            viewModel.updateLocation(lastLocation);
        }

        centerOnLocation();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, viewModel::updateLocation);
    }

    private void centerOnLocation() {
        debug("Entering centerOnLocation");
        handler.post(() -> {
            debug("Posting centerOnLocation");
            Location lastLocation = viewModel.getLastLocation().getValue();
            debug("Last location: " + lastLocation);

            if (lastLocation != null) {
                debug("Centering on location " + lastLocation.getLatitude() + ", " + lastLocation.getLongitude());
                LatLng lastPoint = LocationUtils.getPosition(lastLocation);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPoint, 17f));
            }
        });
    }

    private void updateMap() {
        handler.post(() -> {
            if (map != null && viewModel.getPolylineOptions().getValue() != null) {
                map.clear();
                map.addPolyline(viewModel.getPolylineOptions().getValue());
            }
        });
    }

    private final Runnable updateUIRunnable = new Runnable() {
        @Override
        public void run() {
            viewModel.updateTime();
            handler.postDelayed(this, 1000);
        }
    };

    public void onStopWalk() {
        new Thread(() -> viewModel.stopWalk(getContext())).start();
        navigateToFragment(TabbedFragment.newInstance(), false);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateUIRunnable);
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(updateUIRunnable);
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateUIRunnable);
    }
}