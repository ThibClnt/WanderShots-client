package fr.efrei.wandershots.client.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * This class provides utility methods for locations.
 */
public class LocationUtils {

    /**
     * This method returns a LatLng object from a Location object.
     */
    public static LatLng getPosition(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
