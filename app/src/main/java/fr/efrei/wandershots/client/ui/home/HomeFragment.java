package fr.efrei.wandershots.client.ui.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.HeroCarouselStrategy;

import java.util.Locale;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.data.CredentialsManager;
import fr.efrei.wandershots.client.databinding.FragmentHomeBinding;
import fr.efrei.wandershots.client.entities.User;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.walking.WalkingFragment;


public class HomeFragment extends WandershotsFragment<FragmentHomeBinding> {

    private HomeViewModel homeViewModel;
    private CredentialsManager credentialsManager;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        credentialsManager = CredentialsManager.getInstance(requireContext());

        // Setup the carousel
        HomeCarouselAdapter adapter = new HomeCarouselAdapter(homeViewModel.getTrendingPlaces(), Glide.with(this));
        binding.carouselRecyclerView.setAdapter(adapter);
        binding.carouselRecyclerView.setLayoutManager(new CarouselLayoutManager(new HeroCarouselStrategy()));

        // Greeting message
        User user = credentialsManager.getCredentialsFromCache();
        String username = "Anonymous";

        if (user != null) {
            username = user.getUsername();
        }

        String updated_welcome_message = getString(R.string.welcome_message, username);
        binding.welcomeMessage.setText(updated_welcome_message);

        // Setup the buttons
        binding.startWalk.setOnClickListener(v -> navigateToFragment(WalkingFragment.newInstance()));

        homeViewModel.getTotalDistance().observe(getViewLifecycleOwner(), totalDistance -> {
            String formattedDistance = String.format(Locale.getDefault(), "%.1f", totalDistance / 1000);
            binding.distanceTraveledValue.setText(formattedDistance);
        });
    }
}