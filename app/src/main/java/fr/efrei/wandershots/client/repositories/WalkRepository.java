package fr.efrei.wandershots.client.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.efrei.wandershots.client.datasource.ExternalSQLConnection;
import fr.efrei.wandershots.client.entities.User;
import fr.efrei.wandershots.client.entities.Walk;

/**
 * Repository for the walks of the application.
 * This class is responsible for managing the walks in the database.
 */
public class WalkRepository {

    private static WalkRepository instance;
    private WalkRepository() {}

    /**
     * Get the singleton instance of the walk repository.
     */
    public static synchronized WalkRepository getInstance() {
        if (instance == null) {
            instance = new WalkRepository();
        }
        return instance;
    }

    /**
     * Save a walk in the database. Return the id of the walk.
     */
    public int saveWalk(Walk walk) {
        String insertSQL = "INSERT INTO walk (title, start_time, duration, distance, userId) VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = ExternalSQLConnection.createConnection()) {
            PreparedStatement query = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS) ;
            query.setString(1, walk.getTitle());
            query.setTimestamp(2, new java.sql.Timestamp(walk.getStartTime().getTime()));
            query.setLong(3, walk.getDuration());
            query.setDouble(4, walk.getDistance());
            query.setInt(5, walk.getUserId());
            query.executeUpdate();

            ResultSet generatedKeys = query.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                return -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all the walks of a user.
     */
    public List<Walk> getAllUserWalks(User user) {
        List<Walk> walks = new ArrayList<>();
        String querySQL = "SELECT * FROM walk WHERE userId = ? ORDER BY start_time DESC";

        try (Connection connection = ExternalSQLConnection.createConnection()) {
            PreparedStatement query = connection.prepareStatement(querySQL);
            query.setInt(1, user.getUserId());
            ResultSet rs = query.executeQuery();

            while (rs.next()) {
                Walk walk = new Walk();
                walk.setWalkId(rs.getInt("walk_id"));
                walk.setTitle(rs.getString("title"));
                walk.setStartTime(rs.getTimestamp("start_time"));
                walk.setDuration(rs.getLong("duration"));
                walk.setDistance(rs.getDouble("distance"));
                walks.add(walk);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return walks;
    }
}
