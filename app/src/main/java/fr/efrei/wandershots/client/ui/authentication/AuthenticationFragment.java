package fr.efrei.wandershots.client.ui.authentication;

import fr.efrei.wandershots.client.data.CredentialManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentAuthenticationBinding;
import fr.efrei.wandershots.client.ui.home.HomeFragment;


public class AuthenticationFragment extends Fragment {


    private FragmentAuthenticationBinding binding;

    public static AuthenticationFragment newInstance() {
        return new AuthenticationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAuthenticationBinding.inflate(inflater, container, false);

        // Retrieve text fields and buttons
        TextInputEditText usernameEditText = binding.loginInput;
        TextInputEditText passwordEditText = binding.passwordInput;
        Button loginButton = binding.buttonLogin;
        Button signInButton = binding.buttonSignIn;

        // Define a listener on login button
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            performLogin(username, password);
        });

        // Define a listener on signIn button
        signInButton.setOnClickListener(v -> navigateToSignInFragment());

        return binding.getRoot();
    }


    private void performLogin(String username, String password) {

        if (CredentialManager.authenticate(getContext(), username, password)) {
            // Login succeeded
            String token = CredentialManager.generateToken();
            CredentialManager.saveToken(getContext(), token);
            navigateToHomeFragment();}
        else {
            // Login failed
            Toast.makeText(getContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }
    private void navigateToHomeFragment() {
        // Create an instance of the new fragment
        HomeFragment homeFragment = new HomeFragment();

        // Get the FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the current fragment with the new one
        transaction.replace(R.id.container, homeFragment);

        // Commit the transaction
        transaction.commit();
    }

    private void navigateToSignInFragment() {
        SignInFragment signInFragment = new SignInFragment();

        // Get the FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the current fragment with the new one
        transaction.replace(R.id.container, signInFragment);

        // Commit the transaction
        transaction.commit();
    }






}
