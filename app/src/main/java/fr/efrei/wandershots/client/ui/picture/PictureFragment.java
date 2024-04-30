package fr.efrei.wandershots.client.ui.picture;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

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
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.efrei.wandershots.client.databinding.FragmentPictureBinding;

public class PictureFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String TAG = PictureFragment.class.getSimpleName();

    private PictureViewModel pictureViewModel;
    private FragmentPictureBinding binding;
    private ImageView imageView;
    private String currentPhotoPath;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    public static PictureFragment newInstance() {
        return new PictureFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPictureBinding.inflate(inflater, container, false);
        imageView = binding.pictureImageView;

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Glide.with(this).load(currentPhotoPath).into(imageView);
            }
        });

        binding.takePictureButton.setOnClickListener(v -> dispatchTakePictureIntent());

        binding.savePictureButton.setOnClickListener(v -> savePicture());

        pictureViewModel = new ViewModelProvider(this).get(PictureViewModel.class);

        return binding.getRoot();
    }

    /**
     * Create a file to store the picture
     * @return the Uri of the file
     * @throws IOException
     */
    private Uri createImageFile() throws IOException {
        // Create the file name
        String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + "_" + binding.pictureTitleInput.getText().toString();

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
            return Uri.fromFile(image);

        } else {
            ContentResolver resolver = requireActivity().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            currentPhotoPath = uri.toString();
            return uri;
        }
    }

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Use requestActivity to ask for the camera permission
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        else {
            takePicture();
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            Uri photoURI;
            try {
                photoURI = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
                return;
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                photoURI = FileProvider.getUriForFile(requireActivity(),
                        "fr.efrei.wandershots.client.fileprovider",
                        new File(currentPhotoPath));
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureLauncher.launch(takePictureIntent);
        }
    }

    private void savePicture() {
        // TODO : save picture in the walk
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}