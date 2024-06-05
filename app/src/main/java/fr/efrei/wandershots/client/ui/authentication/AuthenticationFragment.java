package fr.efrei.wandershots.client.ui.authentication;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.mysql.jdbc.StringUtils;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.data.CredentialsManager;
import fr.efrei.wandershots.client.databinding.FragmentAuthenticationBinding;
import fr.efrei.wandershots.client.exceptions.CredentialsManagmentException;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.tabs.TabbedFragment;


public class AuthenticationFragment extends WandershotsFragment<FragmentAuthenticationBinding> {

    private CredentialsManager credentialsManager;
    public static AuthenticationFragment newInstance() {
        return new AuthenticationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.credentialsManager = CredentialsManager.getInstance(getContext());
        this.handler = new Handler();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Define a listener on login button
        binding.buttonLogin.setOnClickListener(v -> onLoginButtonClicked());

        // Define a listener on signIn button
        binding.buttonSignIn.setOnClickListener(v -> navigateToFragment(SignInFragment.newInstance()));

        checkAlreadyAuthenticated();
    }

    private void checkAlreadyAuthenticated() {
        new Thread(() -> {
            try {
                if (credentialsManager.isAuthenticated()) {
                    handler.post(() -> navigateToFragment(TabbedFragment.newInstance()));
                }
            } catch (CredentialsManagmentException e) {
                logError("Failed to check if user is authenticated", e);
                showToastMessage(R.string.error_toast_text);
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

        //Editable usernameEditable = binding.loginInput.getText();
        //Editable passwordEditable = binding.passwordInput.getText();

        // Authenticate the user
        /*new Thread(() -> {
            try {
                //noinspection ConstantConditions
                if (credentialsManager.authenticate(usernameEditable.toString(), passwordEditable.toString())) {
                    handler.post(() -> navigateToFragment(TabbedFragment.newInstance()));
                } else {
                    showToastMessage(R.string.invalid_credentials_toast);
                }
            } catch (CredentialsManagmentException e) {
                logError("Failed to authenticate user", e);
                showToastMessage(R.string.error_toast_text);
            }
        }).start();*/
        navigateToHomeFragment();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean validateField(TextInputEditText field, String errorMessage) {
        if (field.getText() == null || StringUtils.isNullOrEmpty(field.getText().toString())) {
            field.setError(errorMessage);
            return false;
        }
        return true;
    }
}
