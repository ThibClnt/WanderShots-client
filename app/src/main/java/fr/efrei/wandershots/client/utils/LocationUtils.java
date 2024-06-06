package fr.efrei.wandershots.client.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class LocationUtils {
    public static LatLng getPosition(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
