package fr.efrei.wandershots.client.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class to create a connection to the external SQL database.
 */
public class ExternalSQLConnection {

    private static final String url = "jdbc:mysql://10.0.2.2:3306/wandershots";
    private static final String username = "root";
    private static final String password = "";

    /**
     * Creates a connection to the external SQL database.
     *
     * @return the connection to the external SQL database
     * @throws SQLException if an error occurs during the connection
     */
    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
