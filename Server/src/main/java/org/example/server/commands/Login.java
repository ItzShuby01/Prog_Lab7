package org.example.server.commands;

import org.example.common.command.LoginCommand;
import org.example.common.response.Response;
import org.example.server.database.UserDAO;
import java.sql.SQLException;

public class Login implements ServerCommand {
    public static final String DESCRIPTION = "login: Authenticates a user with a username and password.";

    private final UserDAO userDAO;

    public Login(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    public Response execute(LoginCommand commandDto) {
        String username = commandDto.getUsername();
        String password = commandDto.getPassword();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return new Response("Username and password cannot be empty.", false);
        }

        try {
            if (userDAO.checkUserPassword(username, password)) {
                return new Response("User " + username + " logged in successfully!", true);
            } else {
                return new Response("Invalid username or password.", false);
            }
        } catch (SQLException e) {
            return new Response("Database error during login: " + e.getMessage(), false);
        }
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
