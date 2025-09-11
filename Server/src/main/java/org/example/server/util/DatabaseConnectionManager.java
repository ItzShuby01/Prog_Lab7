package org.example.server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private static final String JDBC_URL = "jdbc:postgresql://pg:5432/studs";
    private final String dbUsername;
    private final String dbPassword;

    public DatabaseConnectionManager(String dbUsername, String dbPassword) {
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, dbUsername, dbPassword);
    }
}