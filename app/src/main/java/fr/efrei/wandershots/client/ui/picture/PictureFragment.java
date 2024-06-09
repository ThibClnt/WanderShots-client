package fr.efrei.wandershots.client.ui.picture;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.io.IOException;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentPictureBinding;
import fr.efrei.wandershots.client.entities.Picture;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.walking.WalkingViewModel;
import fr.efrei.wandershots.client.utils.PermissionUtils;

/**
 * This fragment is responsible for the picture screen of the application.
 * It allows the user to take a picture and save it.
 */
public class PictureFragment extends WandershotsFragment<FragmentPictureBinding> {
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private WalkingViewModel walkingViewModel;
    private PictureViewModel pictureViewModel;

    public static PictureFragment newInstance() {
        return new PictureFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        walkingViewModel = new ViewModelProvider(requireActivity()).get(WalkingViewModel.class);
        pictureViewModel = new ViewModelProvider(this).get(PictureViewModel.class);

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Glide
                    .with(this)
                    .load(pictureViewModel.getPath().getValue())
                    .into(binding.pictureImageView);
            }
        });

        // Setup observers
        pictureViewModel.getTitle().observe(getViewLifecycleOwner(), title -> binding.pictureTitleInput.setText(title));
        pictureViewModel.getPath().observe(getViewLifecycleOwner(), path -> {
                debug("New Path : " + path);
                binding.savePictureButton.setEnabled(path != null && !path.toString().isEmpty());
        });

        // Setup buttons
        binding.takePictureButton.setOnClickListener(v -> dispatchTakePictureIntent());
        binding.cancelPictureButton.setOnClickListener(v -> popBackStack());
        binding.savePictureButton.setOnClickListener(v -> savePicture());
    }

    /**
     * This function is called when the user clicks on the take picture button.
     * It dispatches the intent to take a picture, depending on the permissions.
     */
    private void dispatchTakePictureIntent() {
        if (PermissionUtils.hasCameraPermission(requireContext()))
            takePicture();
        else
            PermissionUtils.requestCameraPermission(requireActivity(), this::takePicture);
    }

    /**
     * This function is called when the user takes a picture.
     * It creates a file to store the picture and launches the camera intent.
     */
    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            Uri photoURI;
            try {
                photoURI = pictureViewModel.createPictureFile();
            } catch (IOException ex) {
                logError("Failed to create image file", ex);
                showToastMessage(R.string.error_create_picture);
                return;
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureLauncher.launch(takePictureIntent);
        }
    }

    /**
     * This function is called when the user clicks on the save picture button.
     * It saves the picture in the context of the current walk.
     */
    private void savePicture() {
        try {
            Picture picture = pictureViewModel.popPicture();
            walkingViewModel.addPicture(picture);
            showToastMessage(R.string.picture_saved);
            popBackStack();
        } catch (IOException ex) {
            logError("Failed to save picture.", ex);
            showToastMessage(R.string.error_read_picture);
        }
    }
}