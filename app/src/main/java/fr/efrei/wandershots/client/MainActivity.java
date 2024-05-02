package fr.efrei.wandershots.client;

import static fr.efrei.wandershots.client.ui.walking.WalkingFragment.LOCATION_PERMISSION_REQUEST_CODE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import fr.efrei.wandershots.client.databinding.ActivityMainBinding;
import fr.efrei.wandershots.client.ui.walking.WalkingFragment;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("MyAuthenticationPrefs", Context.MODE_PRIVATE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Merci pour vos permissions", Toast.LENGTH_SHORT).show();
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
                if (fragment instanceof WalkingFragment) {
                    ((WalkingFragment) fragment).displayMyCurrentLocation();
                }
            }
            else {
                Toast.makeText(this, "permissions refusées", Toast.LENGTH_SHORT).show();
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
                if (fragment instanceof WalkingFragment) {
                    ((WalkingFragment) fragment).onStopWalk();
                }

            }
        }
    }

}

