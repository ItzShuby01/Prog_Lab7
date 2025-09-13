package org.example.server.database;

import org.example.common.data.User;
import java.sql.SQLException;

public interface UserDAO {
    User findUserByUsername(String username) throws SQLException;
    boolean createUser(User user) throws SQLException;
    boolean checkUserPassword(String username, String password) throws SQLException;
}
