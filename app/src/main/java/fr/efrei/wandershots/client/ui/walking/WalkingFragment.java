package fr.efrei.wandershots.client.ui.walking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
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

import fr.efrei.wandershots.client.MainActivity;
import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentWalkingBinding;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.picture.PictureFragment;
import fr.efrei.wandershots.client.ui.tabs.TabbedFragment;

public class WalkingFragment extends WandershotsFragment<FragmentWalkingBinding> implements OnMapReadyCallback {

    private GoogleMap mMap;

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
        binding.takePicture.setOnClickListener(v -> navigateToFragment(PictureFragment.newInstance()));
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
            updateMarker(lastKnownLocation);
        }

        LocationListener locationListener = this::updateMarker;

        // Register the location listener with the location manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, locationListener);
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
    //endregion

    public void onStopWalk(){
        // 1 - Save (todo)

        // 2 - Navigate to the home fragment
        navigateToFragment(TabbedFragment.newInstance(), false);
    }
}