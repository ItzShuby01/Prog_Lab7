package org.example.server.commands;

import org.example.common.command.RegisterCommand;
import org.example.common.data.User;
import org.example.common.response.Response;
import org.example.server.database.UserDAOPostgreSQL;
import org.example.server.util.PasswordHasher;

import java.sql.SQLException;

public class Register implements ServerCommand {
    public static final String DESCRIPTION = "register: Registers a new user with a username and password.";

    private final UserDAOPostgreSQL userDAO;

    public Register(UserDAOPostgreSQL userDAO) {
        this.userDAO = userDAO;
    }

    public Response execute(RegisterCommand commandDto) {
        String username = commandDto.getUsername();
        String password = commandDto.getPassword();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return new Response("Username and password cannot be empty.", false);
        }

        try {
            // Check if the username already exists
            User existingUser = userDAO.findUserByUsername(username);
            if (existingUser != null) {
                return new Response("Username already taken. Please choose another.", false);
            }

            // Generate salt and hash the password
            String salt = PasswordHasher.generateSalt();
            String passwordHash = PasswordHasher.hashPassword(password, salt);

            User newUser = new User(username, passwordHash, salt);

            if (userDAO.createUser(newUser)) {
                return new Response("User " + username + " registered successfully!", true);
            } else {
                return new Response("Registration failed. Please try again.", false);
            }

        } catch (SQLException e) {
            return new Response("Database error during registration: " + e.getMessage(), false);
        }
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
