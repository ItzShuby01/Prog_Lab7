package org.example.server.database;

import java.sql.SQLException;
import org.example.common.data.User;

public interface UserDAO {
  User findUserByUsername(String username) throws SQLException;

  boolean createUser(User user) throws SQLException;

  boolean checkUserPassword(String username, String password) throws SQLException;
}
