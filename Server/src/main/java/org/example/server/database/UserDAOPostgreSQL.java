package org.example.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.example.common.data.User;
import org.example.server.util.DatabaseConnectionManager;
import org.example.server.util.PasswordHasher;

public class UserDAOPostgreSQL implements UserDAO {
  private final DatabaseConnectionManager connectionManager;

  public UserDAOPostgreSQL(DatabaseConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }


  //Finds a user by their username.
  // Return an Optional containing the User object if found, or an empty Optional otherwise.
  @Override
  public Optional<User> findUserByUsername(String username) throws SQLException {
    String sql = "SELECT username, password_hash, salt FROM users WHERE username = ?";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, username);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          String passwordHash = rs.getString("password_hash");
          String salt = rs.getString("salt");
          return Optional.of(new User(username, passwordHash, salt));
        }
      }
    }
    return Optional.empty();
  }


  //Creates a new user in the database.
  //Return true if the user was created successfully, false otherwise.
  @Override
  public boolean createUser(User user) throws SQLException {
    String sql = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement stmt = connection.prepareStatement(sql)) {
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


  // Checks if a user's provided password is correct.
  @Override
  public boolean checkUserPassword(String username, String password) throws SQLException {
    Optional<User> userOptional = findUserByUsername(username);
    if (userOptional.isEmpty()) {
      return false;
    }
    User user = userOptional.get();
    return PasswordHasher.checkPassword(password, user.getPasswordHash(), user.getSalt());
  }
}
