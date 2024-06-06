package fr.efrei.wandershots.client;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.function.IntPredicate;

import fr.efrei.wandershots.client.databinding.ActivityMainBinding;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.picture.PictureFragment;
import fr.efrei.wandershots.client.ui.tabs.TabbedFragment;
import fr.efrei.wandershots.client.ui.walking.WalkingFragment;

public class MainActivity extends AppCompatActivity {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final int REQUEST_CAMERA_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if(fragmentManager.getBackStackEntryCount() != 0) {
                    fragmentManager.popBackStack();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        IntPredicate isPermissionGranted = i
                -> grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED;

        boolean granted = true;

        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (fragment instanceof WalkingFragment) {
                    if (isPermissionGranted.test(0)) {
                        ((WalkingFragment) fragment).displayMyCurrentLocation();
                    } else {
                        ((WalkingFragment) fragment).navigateToFragment(TabbedFragment.newInstance());
                        granted = false;
                    }
                }
                break;

                case REQUEST_CAMERA_PERMISSION:
                    if (fragment instanceof PictureFragment) {
                        if (isPermissionGranted.test(0)) {
                            ((PictureFragment) fragment).takePicture();
                        } else {
                            granted = false;
                        }
                    }
                    break;
        }

        if (!granted) {
            ((WandershotsFragment) fragment).showToastMessage(R.string.perm_needed_toast_text);
        }
    }
}
