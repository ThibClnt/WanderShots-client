package fr.efrei.wandershots.client.ui.authentication;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.mysql.jdbc.StringUtils;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.data.CredentialsManager;
import fr.efrei.wandershots.client.databinding.FragmentSignInBinding;
import fr.efrei.wandershots.client.exceptions.CredentialsManagmentException;
import fr.efrei.wandershots.client.ui.WandershotsFragment;


public class SignInFragment extends WandershotsFragment<FragmentSignInBinding> {

    private CredentialsManager credentialsManager;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.credentialsManager = CredentialsManager.getInstance(getContext());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        binding.buttonSave.setOnClickListener(v -> onSignInButtonClicked());
    }

    @SuppressWarnings("ConstantConditions")
    private void onSignInButtonClicked() {
        // Check if the fields are empty
        String fillThisFieldError = getString(R.string.fill_this_field);
        if (!validateField(binding.signInInput, fillThisFieldError) ||
            !validateField(binding.passwordInput, fillThisFieldError) ||
            !validateField(binding.passwordConfirmInput, fillThisFieldError)) {
            return;
        }

        Editable usernameEditable = binding.signInInput.getText();
        Editable passwordEditable = binding.passwordInput.getText();
        Editable repeatPasswordEditable = binding.passwordConfirmInput.getText();

        // Check if the passwords match
        if (!passwordEditable.toString().equals(repeatPasswordEditable.toString())) {
            binding.passwordInput.setError("Passwords do not match");
            binding.passwordConfirmInput.setError("Passwords do not match");
            return;
        }

        // Sign in the user
        new Thread(() -> {
            try {
                if (credentialsManager.signIn(usernameEditable.toString(), passwordEditable.toString())) {
                    handler.post(() -> navigateToFragment(AuthenticationFragment.newInstance()));
                } else {
                    handler.post(() -> {
                        usernameEditable.clear();
                        passwordEditable.clear();
                        repeatPasswordEditable.clear();
                        showToastMessage(R.string.invalid_credentials_toast);
                    });
                }
            } catch (CredentialsManagmentException e) {
                logError("Failed to sign in the user", e);
                showToastMessage(R.string.error_toast_text);
            }
        }).start();
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
