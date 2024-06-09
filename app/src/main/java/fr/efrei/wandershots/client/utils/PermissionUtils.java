package fr.efrei.wandershots.client.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides utility methods for permissions.
 * It allows to check and request permissions.
 * When a permission is requested, a callback can be provided to be executed once the permission is granted.
 */
public class PermissionUtils {

    public final static int CAMERA_PERMISSION_REQUEST_CODE = 1;
    public final static int LOCATION_PERMISSION_REQUEST_CODE = 2;

    private final static Map<Integer, Runnable> onGrantedCallbacks = new HashMap<>();

    /**
     * This method checks if the camera permission is granted.
     */
    public static boolean hasCameraPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * This method requests the camera permission.
     * It takes an activity and a callback to be executed once the permission is granted.
     */
    public static void requestCameraPermission(Activity activity, Runnable onGrantedCallback) {
        ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        onGrantedCallbacks.put(CAMERA_PERMISSION_REQUEST_CODE, onGrantedCallback);
    }

    /**
     * This method checks if the location permission is granted.
     * It checks both the fine and coarse location permissions.
     */
    public static boolean hasLocationPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * This method requests the location permission.
     * It takes an activity and a callback to be executed once the permission is granted.
     */
    public static void requestLocationPermission(Activity activity, Runnable onGrantedCallback) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
        onGrantedCallbacks.put(LOCATION_PERMISSION_REQUEST_CODE, onGrantedCallback);
    }

    /**
     * This method returns the callback to be executed once the permission is granted.
     * It takes the request code, the permissions, and the grant results.
     * It returns the callback if the permission is granted, otherwise it returns null.
     */
    public static Runnable getGrantedCallbackOrNull(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && onGrantedCallbacks.containsKey(requestCode)) {
            return onGrantedCallbacks.get(requestCode);
        } else {
            return null;
        }
    }
}
