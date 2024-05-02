package fr.efrei.wandershots.client.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ExternalSQLConnection {

    private static final String url = "jdbc:mysql://10.0.2.2:3306/wandershots";
    private static final String username = "root";
    private static final String password = "";

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
