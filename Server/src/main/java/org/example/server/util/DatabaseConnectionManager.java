package org.example.server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {
  private final String jdbcUrl;
  private final String dbUsername;
  private final String dbPassword;

  public DatabaseConnectionManager(String jdbcUrl, String dbUsername, String dbPassword) {
    this.jdbcUrl = jdbcUrl;
    this.dbUsername = dbUsername;
    this.dbPassword = dbPassword;
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
  }
}
