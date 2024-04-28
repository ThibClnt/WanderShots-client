package fr.efrei.wandershots.client.ui.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.data.CredentialManager;
import fr.efrei.wandershots.client.databinding.FragmentSignInBinding;


public class SignInFragment extends Fragment {
    private FragmentSignInBinding binding;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false);

        TextInputEditText usernameEditText = binding.signInInput;
        TextInputEditText passwordEditText = binding.passwordInput;
        TextInputEditText passwordConfirmEditText = binding.passwordConfirmInput;
        Button saveButton = binding.buttonSave;

        saveButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = passwordConfirmEditText.getText().toString();

            performSignIn(username, password,confirmPassword);
        });

        return binding.getRoot();
    }
    private void performSignIn(String username, String password, String confirmPassword) {

        // Validate username, password, and confirm password
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            // Display an error message or handle invalid credentials scenario
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            // Display an error message or handle password mismatch scenario
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (CredentialManager.saveCredentials(getContext(), username, password)) {
            Toast.makeText(getContext(), "Compte créé avec succès", Toast.LENGTH_SHORT).show();
            navigateToLoginFragment();

        } else {
            Toast.makeText(getContext(), "Échec de la création du compte", Toast.LENGTH_SHORT).show();
        }


    }
    private void navigateToLoginFragment() {
        AuthenticationFragment authenticationFragment = new AuthenticationFragment();

        // Get the FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the current fragment with the new one
        transaction.replace(R.id.container, authenticationFragment);

        // Commit the transaction
        transaction.commit();
    }

}
