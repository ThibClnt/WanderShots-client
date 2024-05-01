package fr.efrei.wandershots.client.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import fr.efrei.wandershots.client.entities.User;
import fr.efrei.wandershots.client.exceptions.CredentialsManagmentException;
import fr.efrei.wandershots.client.repositories.UserRepository;

public class CredentialsManager {
    private static final String SHARED_PREF_NAME = "LoginPrefs";

    private static final String KEY_USERID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private final UserRepository userRepository;
    private final SharedPreferences sharedPreferences;


    public CredentialsManager(Context context) {
        this.userRepository = UserRepository.getInstance();
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * This function checks if the credentials are correct and put them in cache if they are
     */
    public boolean authenticate(String username, String password) throws CredentialsManagmentException {
        String hashedPassword = hashPassword(password);
        return authenticate(username, hashedPassword, true);
    }

    /**
     * This function checks if the credentials are correct, and possibly put them in cache if they are
     */
    private boolean authenticate(String username, String hashedPassword, boolean putInCache) throws CredentialsManagmentException {
        try {
            User user = userRepository.getUser(username);

            if (user == null) {
                return false;
            }

            if (putInCache) {
                cacheCredentials(user);
            }

            return true;

        } catch (SQLException e) {
            throw new CredentialsManagmentException("Error while checking credentials", e);
        }
    }

    /**
     * This function check if the credentials are stored in cache and if they are correct
     */
    public boolean isAuthenticated() throws CredentialsManagmentException {
        User user = getCredentialsFromCache();
        return authenticate(user.getUsername(), user.getPassword(), false);
    }

    /**
     * This function put the credentials in cache in order to avoid asking the user to login every time the app is opened
     */
    public void cacheCredentials(User user) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putInt(KEY_USERID, user.getUserId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_PASSWORD, user.getPassword());
        editor.apply();
    }

    /**
     * This function try to get credentials from cache and return the corresponding User object if it exists
     */
    public User getCredentialsFromCache() {
        int userId = this.sharedPreferences.getInt(KEY_USERID, -1);
        String username = this.sharedPreferences.getString(KEY_USERNAME, null);
        String password = this.sharedPreferences.getString(KEY_PASSWORD, null);

        if (userId == -1 || username == null || password == null) {
            return null;
        }

        return new User(userId, username, password);
    }

    public boolean signIn(String username, String password) throws CredentialsManagmentException {
        try {
            String hashedPassword = hashPassword(password);
            User user = new User(username, hashedPassword);
            return userRepository.createUser(user);
        } catch (SQLException e) {
            throw new CredentialsManagmentException("Error while creating user", e);
        }
    }

    /**
     * This function hashes the password using SHA-256 algorithm
     */
    private static String hashPassword(String password) throws CredentialsManagmentException {
        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new CredentialsManagmentException("Error while hashing password", e);
        }
    }
}
