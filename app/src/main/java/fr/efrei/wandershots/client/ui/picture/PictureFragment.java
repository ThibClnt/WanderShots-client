package fr.efrei.wandershots.client.ui.picture;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.HeroCarouselStrategy;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentHomeBinding;
import fr.efrei.wandershots.client.databinding.FragmentPictureBinding;
import fr.efrei.wandershots.client.ui.home.HomeCarouselAdapter;
import fr.efrei.wandershots.client.ui.home.HomeViewModel;

public class PictureFragment extends Fragment {

    private PictureViewModel pictureViewModel;
    private FragmentPictureBinding binding;

    public static PictureFragment newInstance() {
        return new PictureFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPictureBinding.inflate(inflater, container, false);

        pictureViewModel = new ViewModelProvider(this).get(PictureViewModel.class);

        return binding.getRoot();
    }
}