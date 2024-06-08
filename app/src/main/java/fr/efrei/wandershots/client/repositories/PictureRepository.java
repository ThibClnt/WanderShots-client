package fr.efrei.wandershots.client.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.efrei.wandershots.client.datasource.ExternalSQLConnection;
import fr.efrei.wandershots.client.entities.Picture;

public class PictureRepository {

    private static PictureRepository instance;

    private PictureRepository() {}

    /**
     * Get the singleton instance of the picture repository.
     */
    public static synchronized PictureRepository getInstance() {
        if (instance == null) {
            instance = new PictureRepository();
        }
        return instance;
    }

    /**
     * Save a picture in the database
     */
    public void savePicture(Picture picture) {
        String insertSQL = "INSERT INTO Picture (walk_id, latitude, longitude,  title, image) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ExternalSQLConnection.createConnection()) {
            PreparedStatement query = connection.prepareStatement(insertSQL);
            query.setInt(1, picture.getWalkId());
            query.setDouble(2, picture.getLatitude());
            query.setDouble(3, picture.getLongitude());
            query.setString(4, picture.getTitle());
            query.setBytes(5, picture.getImage());
            query.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all the pictures of a user.
     * @param userId The id of the user.
     * @return The list of pictures of the user.
     */
    public List<Picture> getAllUserPictures(int userId) {
        String selectSQL = "SELECT * FROM Picture JOIN Walk ON Picture.walk_id = Walk.walk_id WHERE Walk.userId = ?";
        List<Picture> pictures = new ArrayList<>();

        try (Connection connection = ExternalSQLConnection.createConnection()) {
            PreparedStatement query = connection.prepareStatement(selectSQL);
            query.setInt(1, userId);
            ResultSet rs = query.executeQuery();

            while (rs.next()) {
                Picture picture = new Picture();
                picture.setPictureId(rs.getInt("picture_id"));
                picture.setWalkId(rs.getInt("walk_id"));
                picture.setLatitude(rs.getDouble("latitude"));
                picture.setLongitude(rs.getDouble("longitude"));
                picture.setTitle(rs.getString("title"));
                picture.setImage(rs.getBytes("image"));
                pictures.add(picture);
            }

            return pictures;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
