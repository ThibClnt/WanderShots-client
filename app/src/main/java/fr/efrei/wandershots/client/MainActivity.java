package fr.efrei.wandershots.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import fr.efrei.wandershots.client.databinding.ActivityMainBinding;
import fr.efrei.wandershots.client.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        getOnBackPressedDispatcher().addCallback(this, getOnBackPressedCallback());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Runnable onGrantedCallback = PermissionUtils.getGrantedCallbackOrNull(requestCode, permissions, grantResults);
        if (onGrantedCallback == null) {
            Toast.makeText(getApplicationContext(), R.string.perm_needed_toast_text, Toast.LENGTH_SHORT).show();
        } else {
            onGrantedCallback.run();
        }
    }

    public OnBackPressedCallback getOnBackPressedCallback() {
        return new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if(fragmentManager.getBackStackEntryCount() != 0) {
                    fragmentManager.popBackStack();
                }
            }
        };
    }
}
