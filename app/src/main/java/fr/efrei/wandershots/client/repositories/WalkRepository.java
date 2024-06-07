package fr.efrei.wandershots.client.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.efrei.wandershots.client.datasource.ExternalSQLConnection;
import fr.efrei.wandershots.client.entities.User;
import fr.efrei.wandershots.client.entities.Walk;

public class WalkRepository {

    private static WalkRepository instance;
    private WalkRepository() {}

    public static synchronized WalkRepository getInstance() {
        if (instance == null) {
            instance = new WalkRepository();
        }
        return instance;
    }

    public void saveWalk(Walk walk) {
        String insertSQL = "INSERT INTO walk (title, start_time, duration, distance, userId) VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = ExternalSQLConnection.createConnection()) {
            PreparedStatement query = connection.prepareStatement(insertSQL) ;
            query.setString(1, walk.getTitle());
            query.setTimestamp(2, new java.sql.Timestamp(walk.getStartTime().getTime()));
            query.setLong(3, walk.getDuration());
            query.setDouble(4, walk.getDistance());
            query.setInt(5, walk.getUserId());
            query.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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
