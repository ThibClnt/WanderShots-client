package fr.efrei.wandershots.client.ui.authentication;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.mysql.jdbc.StringUtils;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.data.CredentialsManager;
import fr.efrei.wandershots.client.databinding.FragmentSignInBinding;
import fr.efrei.wandershots.client.exceptions.CredentialsManagmentException;


public class SignInFragment extends Fragment {

    private static final String TAG = SignInFragment.class.getSimpleName();

    private FragmentSignInBinding binding;
    private CredentialsManager credentialsManager;
    private Handler handler;


    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.binding = FragmentSignInBinding.inflate(inflater, container, false);
        this.credentialsManager = CredentialsManager.getInstance(getContext());
        this.handler = new Handler();
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        binding.buttonSave.setOnClickListener(v -> onSignInButtonClicked());
    }

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
                    handler.post(this::navigateToLoginFragment);
                } else {
                    handler.post(() -> {
                        usernameEditable.clear();
                        passwordEditable.clear();
                        repeatPasswordEditable.clear();
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

    private void navigateToLoginFragment() {
        AuthenticationFragment authenticationFragment = new AuthenticationFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, authenticationFragment)
                .commit();
    }
}
