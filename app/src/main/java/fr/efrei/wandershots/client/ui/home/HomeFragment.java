package fr.efrei.wandershots.client.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.HeroCarouselStrategy;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentHomeBinding;
import fr.efrei.wandershots.client.ui.authentication.AuthenticationFragment;
import fr.efrei.wandershots.client.ui.tabs.TabbedFragment;
import fr.efrei.wandershots.client.ui.walking.WalkingFragment;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        HomeCarouselAdapter adapter = new HomeCarouselAdapter(homeViewModel.getTrendingPlaces(), Glide.with(this));
        binding.carouselRecyclerView.setAdapter(adapter);
        binding.carouselRecyclerView.setLayoutManager(new CarouselLayoutManager(new HeroCarouselStrategy()));

        Button startButton = binding.startWalk;
        // Ajouter un écouteur de clic au bouton startWalk
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Appeler la méthode pour afficher le fragment WalkingFragment
                navigateWalkingFragment();
            }
        });
        return binding.getRoot();
    }
    private void navigateWalkingFragment() {
        WalkingFragment walkingFragment = WalkingFragment.newInstance();

        // Get the FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the current fragment with the new one
        transaction.replace(R.id.container, walkingFragment);

        // Commit the transaction
        transaction.commit();
    }


}