package org.example.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.common.data.User;
import org.example.server.util.PasswordHasher;

public class UserDAOPostgreSQL implements UserDAO {
  private final Connection connection;

  public UserDAOPostgreSQL(Connection connection) {
    this.connection = connection;
  }

  @Override
  public User findUserByUsername(String username) throws SQLException {
    String sql = "SELECT * FROM users WHERE username = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, username);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          Long id = rs.getLong("id");
          String passwordHash = rs.getString("password_hash");
          String salt = rs.getString("salt");
          return new User(id, username, passwordHash, salt);
        }
      }
    }
    return null;
  }

  @Override
  public boolean createUser(User user) throws SQLException {
    String sql = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, user.getUsername());
      stmt.setString(2, user.getPasswordHash());
      stmt.setString(3, user.getSalt());
      int rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      if (e.getSQLState().equals("23505")) { // 23505 is the unique_violation SQLState
        return false;
      }
      throw e;
    }
  }

  @Override
  public boolean checkUserPassword(String username, String password) throws SQLException {
    User user = findUserByUsername(username);
    if (user == null) {
      return false;
    }
    return PasswordHasher.checkPassword(password, user.getPasswordHash(), user.getSalt());
  }
}
