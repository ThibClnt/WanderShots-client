package fr.efrei.wandershots.client.ui.walking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
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
    private LocationManager locationManager;
    private boolean isTracking = true;

    public static WalkingFragment newInstance() {
        return new WalkingFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WalkingViewModel.class);

        // When back press : reset the viewModel without saving, and navigate back
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.reset();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                if(fragmentManager.getBackStackEntryCount() != 0) {
                    fragmentManager.popBackStack();
                }
            }
        });

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

        viewModel.getPictures().observe(getViewLifecycleOwner(), pictures ->
                debug("New pictures : " + pictures.size()));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setBuildingsEnabled(true);

        if (PermissionUtils.hasLocationPermission(requireContext())) {
            locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            startLocationTracking();
        } else {
            PermissionUtils.requestLocationPermission(requireActivity(), this::startLocationTracking);
        }
    }

    @SuppressLint("MissingPermission")
    public void startLocationTracking() {
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(() -> {
            isTracking = true;
            centerOnLocation();
            return true;
        });
        map.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                isTracking = false;
            }
        });

        debug("Starting location tracking");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, onLocationUpdate);
    }

    private final LocationListener onLocationUpdate = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            viewModel.updateLocation(location);
            if (isTracking)
                centerOnLocation();
        }
    };

    private void centerOnLocation() {
        handler.post(() -> {
            Location lastLocation = viewModel.getLastLocation().getValue();

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
            debug("Update UI");
            viewModel.updateTime();
            handler.postDelayed(this, 1000);
        }
    };

    public void onStopWalk() {
        new Thread(() -> viewModel.stopWalk(getContext())).start();
        navigateToFragment(TabbedFragment.newInstance(), false);
        viewModel.reset();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateUIRunnable);
        mapView.onPause();
        locationManager.removeUpdates(onLocationUpdate);
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