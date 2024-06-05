package fr.efrei.wandershots.client.ui.picture;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import fr.efrei.wandershots.client.MainActivity;
import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentPictureBinding;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.walking.WalkingFragment;

public class PictureFragment extends WandershotsFragment<FragmentPictureBinding> {
    private ImageView imageView;
    private String currentPhotoPath;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    public static PictureFragment newInstance() {
        return new PictureFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Launch the camera and display the picture
        imageView = binding.pictureImageView;
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Glide.with(this).load(currentPhotoPath).into(imageView);
            }
        });

        // Setup buttons
        binding.takePictureButton.setOnClickListener(v -> dispatchTakePictureIntent());
        binding.savePictureButton.setOnClickListener(v -> savePicture());
    }

    private Uri createImageFile() throws IOException {
        // Create the file name
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";

        // If the device is running on Android 9 (API 28) or lower, we use the File API
        // Otherwise, we have to use the MediaStore API
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
            currentPhotoPath = image.getAbsolutePath();
            return FileProvider.getUriForFile(requireActivity(),
                    "fr.efrei.wandershots.client.fileprovider",
                    new File(currentPhotoPath));

        } else {
            ContentResolver resolver = requireActivity().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            if (uri == null) {
                throw new IOException("Failed to create new MediaStore record.");
            }

            currentPhotoPath = uri.toString();
            return uri;
        }
    }

    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            Uri photoURI;
            try {
                photoURI = createImageFile();
            } catch (IOException ex) {
                logError("Failed to create image file", ex);
                showToastMessage(R.string.error_create_picture);
                return;
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureLauncher.launch(takePictureIntent);
        }
    }

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.CAMERA}, MainActivity.REQUEST_CAMERA_PERMISSION);
        }
        else {
            takePicture();
        }
    }

    private void savePicture() {
        // TODO : save picture in the walk

        navigateToFragment(WalkingFragment.newInstance(), false);
    }
}