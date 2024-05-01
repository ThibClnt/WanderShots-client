package fr.efrei.wandershots.client.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fr.efrei.wandershots.client.datasource.ExternalSQLConnection;
import fr.efrei.wandershots.client.entities.User;

public class UserRepository {
    // Singleton pattern

    private static volatile UserRepository instance;

    /**
     * Get the singleton instance of the user repository.
     */
    public static UserRepository getInstance() {
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
        }
        return instance;
    }

    //region Get user methods

    /**
     * Get a user from a result set, or null if the result set is empty.
     */
    private User getUserFromResult(ResultSet resultSet) throws SQLException {

        if (!resultSet.first()) {
            return null;
        }

        User user = new User();
        user.setUserId(resultSet.getInt("userId"));
        user.setUsername(resultSet.getString("username"));
        return user;
    }

    /**
     * Get a user by its id, or null if the user does not exist.
     * The password is never returned.
     */
    public User getUser(int userId) throws SQLException {

        try (Connection connection = ExternalSQLConnection.createConnection()) {
            PreparedStatement query = connection.prepareStatement("SELECT userId, username FROM users WHERE id = ?");
            query.setInt(1, userId);
            return getUserFromResult(query.executeQuery());
        }
    }

    /**
     * Get a user by its username, or null if the user does not exist.
     * The password is never returned.
     */
    public User getUser(String username) throws SQLException {

        try (Connection connection = ExternalSQLConnection.createConnection()) {
            PreparedStatement query = connection.prepareStatement("SELECT userId, username FROM users WHERE username = ?");
            query.setString(1, username);
            return getUserFromResult(query.executeQuery());
        }
    }

    /**
     * Get a user by its credentials, or null if the user does not exist.
     * The password is never returned.
     */
    public User getUser(String username, String password) throws SQLException {

        try (Connection connection = ExternalSQLConnection.createConnection()) {
            PreparedStatement query = connection.prepareStatement("SELECT userId, username FROM users WHERE username = ? AND password = ?");
            query.setString(1, username);
            query.setString(2, password);
            return getUserFromResult(query.executeQuery());
        }
    }
    //endregion

    //region Create user methods

    /**
     * Create a user in the database.
     * Returns true if the user was created, false if the user already exists.
     */
    public boolean createUser(User user) throws SQLException {

        try (Connection connection = ExternalSQLConnection.createConnection()) {
            if (getUser(user.getUsername()) != null) {
                return false;
            }

            PreparedStatement query = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            query.setString(1, user.getUsername());
            query.setString(2, user.getPassword());
            query.executeUpdate();
            return true;
        }
    }

    //endregion
}
