package fr.efrei.wandershots.client.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class CredentialManager {
    private static final String CREDENTIAL_FILE_NAME = "credentials.txt";
    private static final String SHARED_PREF_NAME = "LoginPrefs";
    private static final String KEY_TOKEN = "token";

    // this function is called once when the user signs in the app
    public static boolean saveCredentials(Context context, String username, String password) {
        try {
            File file = new File(context.getFilesDir(), CREDENTIAL_FILE_NAME);
            String hashedPassword = hashPassword(password);
            String credentials = username + ":" + hashedPassword;
            FileOutputStream fos = context.openFileOutput(CREDENTIAL_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(credentials.getBytes());
            fos.close();
            return true;
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
    // this function is called every time connection button is hit
    public static boolean authenticate(Context context, String username, String password) {
        try {
            String hashedPassword = hashPassword(password);
            String savedCredentials = readCredentials(context);
            String[] parts = savedCredentials.split(":");
            if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(hashedPassword)) {
                return true;
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    // this function is called when connection is established
    public static String generateToken() {

        return UUID.randomUUID().toString();
    }

    // this function is called when connection is established
    public static void saveToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    // this function is called when we need to check user authentication
    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // algorithm to hash password
    private static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // this function is called every time loginIn button is hit to read credentials stored in CREDENTIAL_FILE_NAME
    private static String readCredentials(Context context) throws IOException {
        FileInputStream fis = context.openFileInput(CREDENTIAL_FILE_NAME);
        int size = fis.available();
        byte[] buffer = new byte[size];
        fis.read(buffer);
        fis.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }

}
