package fr.efrei.wandershots.client.repositories;

import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import fr.efrei.wandershots.client.datasource.ExternalSQLConnection;
import fr.efrei.wandershots.client.entities.Picture;

public class PictureRepository {

    private static PictureRepository instance;

    private PictureRepository() {}

    public static synchronized PictureRepository getInstance() {
        if (instance == null) {
            instance = new PictureRepository();
        }
        return instance;
    }

    public void savePicture(Picture picture) {
        String insertSQL = "INSERT INTO Picture (walk_id, title, image) VALUES (?, ?, ?)";

        try (Connection connection = ExternalSQLConnection.createConnection()) {
            PreparedStatement query = connection.prepareStatement(insertSQL);
            query.setInt(1, picture.getWalkId());
            query.setString(2, picture.getTitle());
            query.setBytes(3, picture.getImage());
            query.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
