package fr.efrei.wandershots.client.ui.walking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentWalkingBinding;
import fr.efrei.wandershots.client.ui.home.HomeFragment;

public class WalkingFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mMapView;
    private FragmentWalkingBinding binding;

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public static WalkingFragment newInstance() {
        return new WalkingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWalkingBinding.inflate(inflater, container, false);

        Button stopWalk = binding.stopWalk;
        stopWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopWalk();
            }
        });
        mMapView = binding.mapView;
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        return binding.getRoot();
    }

    public void onStopWalk(){
        HomeFragment homeFragment = HomeFragment.newInstance();

        // Get the FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the current fragment with the new one
        transaction.replace(R.id.container, homeFragment);

        // Commit the transaction
        transaction.commit();
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);

        // Request location permissions if necessary
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Nous avons besoin de vos permissions", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            displayMyCurrentLocation();
        }
    }


    @SuppressLint("MissingPermission")
    public void displayMyCurrentLocation() {
        if (mMap != null) {
            LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                // Retrieve the last known location
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    // create an object latitude longitude
                    LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    // add a marker
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Ma position"));
                    // move camera to user location with zoom
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));
                }

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        // When the location changes, update the marker and move the camera
                        LatLng updatedUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.clear(); // Clear previous markers
                        mMap.addMarker(new MarkerOptions().position(updatedUserLocation).title("Ma position"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(updatedUserLocation, 15f));
                    }
                };

                // Register the location listener with the location manager to receive location updates

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
}






