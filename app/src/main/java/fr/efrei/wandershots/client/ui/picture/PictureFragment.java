package fr.efrei.wandershots.client.ui.picture;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.databinding.FragmentPictureBinding;
import fr.efrei.wandershots.client.entities.Picture;
import fr.efrei.wandershots.client.ui.WandershotsFragment;
import fr.efrei.wandershots.client.ui.walking.WalkingViewModel;
import fr.efrei.wandershots.client.utils.PermissionUtils;
import fr.efrei.wandershots.client.utils.TimeUtils;

public class PictureFragment extends WandershotsFragment<FragmentPictureBinding> {
    private ImageView imageView;
    private String currentPhotoPath;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private WalkingViewModel walkingViewModel;

    public static PictureFragment newInstance() {
        return new PictureFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Launch the camera and display the picture
        imageView = binding.pictureImageView;
        walkingViewModel = new ViewModelProvider(requireActivity()).get(WalkingViewModel.class);
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
        if (PermissionUtils.hasCameraPermission(requireContext()))
            takePicture();
        else
            PermissionUtils.requestCameraPermission(requireActivity(), this::takePicture);
    }

    private void savePicture() {
        // TODO : save picture in the walk
        if (currentPhotoPath == null) {
            showToastMessage(R.string.error_no_picture);
            return;
        }

        EditText titleInput = binding.pictureTitleInput;
        String title;
        if (titleInput.getText().toString().isEmpty()) {
            title = "default_picture_title";
        } else {
            title = titleInput.getText().toString();
        }

        // Read image file as byte array
        byte[] image = null;
        try {
            image = readFileAsByteArray(currentPhotoPath);
        } catch (IOException e) {
            logError("Failed to read image file", e);
            showToastMessage(R.string.error_read_picture);
            return;
        }

        // Create Picture entity and save it to ViewModel
        Picture picture = new Picture();
        // set only title and image
        picture.setTitle(title);
        picture.setImage(image);
        // walkingViewModel will set idWalk
        walkingViewModel.addPicture(picture);

        showToastMessage(R.string.picture_saved);

        // Clear the current photo path and title input
        currentPhotoPath = null;
        binding.pictureTitleInput.setText("");
        popBackStack();
    }
    private byte[] readFileAsByteArray(String filePath) throws IOException {
        File file = new File(filePath);
        try (InputStream inputStream = new FileInputStream(file);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
}