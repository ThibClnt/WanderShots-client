package fr.efrei.wandershots.client.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.HeroCarouselStrategy;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentHomeBinding;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.walking.WalkingFragment;


public class HomeFragment extends WandershotsFragment<FragmentHomeBinding> {

    private HomeViewModel homeViewModel;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Setup the carousel
        HomeCarouselAdapter adapter = new HomeCarouselAdapter(homeViewModel.getTrendingPlaces(), Glide.with(this));
        binding.carouselRecyclerView.setAdapter(adapter);
        binding.carouselRecyclerView.setLayoutManager(new CarouselLayoutManager(new HeroCarouselStrategy()));

        // TODO retreive user name
        String updated_welcome_message = getString(R.string.welcome_message, "toto");
        binding.welcomeMessage.setText(updated_welcome_message);

        // Setup the buttons
        binding.startWalk.setOnClickListener(v -> navigateToFragment(WalkingFragment.newInstance()));
    }
}