package fr.efrei.wandershots.client.ui.authentication;

import fr.efrei.wandershots.client.data.CredentialsManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.mysql.jdbc.StringUtils;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentAuthenticationBinding;
import fr.efrei.wandershots.client.exceptions.CredentialsManagmentException;
import fr.efrei.wandershots.client.ui.tabs.TabbedFragment;


public class AuthenticationFragment extends Fragment {

    private static final String TAG = AuthenticationFragment.class.getSimpleName();

    private FragmentAuthenticationBinding binding;
    private CredentialsManager credentialsManager;
    private Handler handler;

    public static AuthenticationFragment newInstance() {
        return new AuthenticationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentAuthenticationBinding.inflate(inflater, container, false);
        this.credentialsManager = CredentialsManager.getInstance(getContext());
        this.handler = new Handler();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Define a listener on login button
        binding.buttonLogin.setOnClickListener(v -> onLoginButtonClicked());

        // Define a listener on signIn button
        binding.buttonSignIn.setOnClickListener(v -> navigateToSignInFragment());

        checkAlreadyAuthenticated();
    }

    private void checkAlreadyAuthenticated() {
        new Thread(() -> {
            try {
                if (credentialsManager.isAuthenticated()) {
                    handler.post(this::navigateToHomeFragment);
                }
            } catch (CredentialsManagmentException e) {
                Log.e(TAG, e.getMessage(), e);
                handler.post(() -> Toast.makeText(getContext(), R.string.error_toast_text, Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    private void onLoginButtonClicked() {
        // Check if the fields are empty
        String fillThisFieldError = getString(R.string.fill_this_field);
        if (!validateField(binding.loginInput, fillThisFieldError) ||
                !validateField(binding.passwordInput, fillThisFieldError)) {
            return;
        }


        Editable usernameEditable = binding.loginInput.getText();
        Editable passwordEditable = binding.passwordInput.getText();

        // Authenticate the user
        new Thread(() -> {
            try {
                if (credentialsManager.authenticate(usernameEditable.toString(), passwordEditable.toString())) {
                    handler.post(this::navigateToHomeFragment);
                } else {
                    handler.post(() -> {
                        passwordEditable.clear();
                        Toast.makeText(getContext(), R.string.invalid_credentials_toast, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (CredentialsManagmentException e) {
                Log.e(TAG, e.getMessage(), e);
                handler.post(() -> Toast.makeText(getContext(), R.string.error_toast_text, Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private boolean validateField(TextInputEditText field, String errorMessage) {
        if (field.getText() == null || StringUtils.isNullOrEmpty(field.getText().toString())) {
            field.setError(errorMessage);
            return false;
        }
        return true;
    }

    //region Navigation methods
    private void navigateToHomeFragment() {
        TabbedFragment homeFragment = TabbedFragment.newInstance();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, homeFragment)
                .commit();
    }

    private void navigateToSignInFragment() {
        SignInFragment signInFragment = new SignInFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, signInFragment)
                .commit();
    }
    //endregion
}
