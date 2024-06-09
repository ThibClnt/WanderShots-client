package fr.efrei.wandershots.client.ui.picture;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import fr.efrei.wandershots.client.entities.Picture;
import fr.efrei.wandershots.client.utils.TimeUtils;

/**
 * This class is the ViewModel for the PictureFragment.
 * It is responsible for managing the data of the picture screen of the application. */
public class PictureViewModel extends AndroidViewModel {

    private final MutableLiveData<String> titleLiveData = new MutableLiveData<>(getDefaultTitle());
    public LiveData<String> getTitle() { return titleLiveData; }

    private final MutableLiveData<Uri> pathLiveData = new MutableLiveData<>();
    public LiveData<Uri> getPath() { return pathLiveData; }

    public PictureViewModel(Application application) {
        super(application);
    }

    public void setTitle(String title) {
        titleLiveData.setValue(title);
    }

    public void setPath(Uri path) {
        pathLiveData.setValue(path);
    }

    /**
     * Create a new picture file and return the URI of the file.
     * The file is created in the external pictures directory of the application or in the MediaStore, depending on the Android version.
     */
    public Uri createPictureFile() throws IOException {
        // Create the file name
        String imageFileName = "JPEG_" + titleLiveData.getValue() + "_";

        // If the device is running on Android 9 (API 28) or lower, we use the File API
        // Otherwise, we have to use the MediaStore API
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            File storageDir = getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );

            Uri path = FileProvider.getUriForFile(getApplication(),
                    "fr.efrei.wandershots.client.fileprovider",
                    new File(image.getAbsolutePath()));

            pathLiveData.setValue(path);
            return path;

        } else {
            ContentResolver resolver = getApplication().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            if (uri == null) {
                throw new IOException("Failed to create new MediaStore record.");
            }

            pathLiveData.setValue(uri);
            return uri;
        }
    }

    /**
     * Get the bytes array of the picture from the URI.
     */
    private byte[] getPictureBytes() throws IOException {
        Uri uri = pathLiveData.getValue();
        if (uri == null) {
            throw new FileNotFoundException("No picture path found");
        }

        ContentResolver contentResolver = getApplication().getContentResolver();

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             InputStream inputStream = contentResolver.openInputStream(uri)) {

            if (inputStream == null) {
                throw new FileNotFoundException("Unable to open input stream for URI: " + uri);
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * Create a new Picture object with the current title and picture data, and reset the title and path.
     */
    public Picture popPicture() throws IOException {
        Picture picture = new Picture();
        picture.setTitle(titleLiveData.getValue() == null || titleLiveData.getValue().isEmpty() ? getDefaultTitle() : titleLiveData.getValue());
        picture.setImage(getPictureBytes());

        // Clear the current photo path and title input
        pathLiveData.setValue(null);
        titleLiveData.setValue(getDefaultTitle());

        return picture;
    }

    /**
     * Get the default title for a picture (if no title is provided).
     */
    private static String getDefaultTitle() {
        return "Wandershots - " + TimeUtils.getCurrentDateTimeString();
    }
}
