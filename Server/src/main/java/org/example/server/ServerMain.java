package org.example.server;

import org.example.common.util.Logger;
import org.example.server.database.PersonDAOPostgreSQL;
import org.example.server.manager.CollectionManager;
import org.example.server.database.UserDAOPostgreSQL;
import org.example.server.manager.ServerCommandManager;
import org.example.server.util.DatabaseConnectionManager;
import org.example.server.network.UDPServer;
import java.sql.Connection;
import java.sql.SQLException;

// Starts the server, handles database connections, and manages the collection.
public class ServerMain {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar server.jar <db_username> <db_password>");
            return;
        }

        String dbUsername = args[0];
        String dbPassword = args[1];

        UDPServer server = null;
        Connection connection = null;

        try {
            // Establish database connection and DAO
            DatabaseConnectionManager dbConnectionManager = new DatabaseConnectionManager(dbUsername, dbPassword);
            connection = dbConnectionManager.getConnection();


            PersonDAOPostgreSQL personDAO = new PersonDAOPostgreSQL(dbConnectionManager);
            UserDAOPostgreSQL userDAO = new UserDAOPostgreSQL(connection);

            //Initialize CollectionManager with the DAO
            CollectionManager collectionManager = new CollectionManager(personDAO);
            ServerCommandManager serverCommandManager = new ServerCommandManager(collectionManager, userDAO);
            Logger.info("Collection loaded from database successfully!");

            // Start the server
            server = new UDPServer(10000, serverCommandManager);
            server.start();

        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred during server operation: " + e.getMessage());
        } finally {
            if (server != null) {
                //Graceful shutdown to release all resources
                server.stop();
            }
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    System.err.println("Error closing database connection: " + e.getMessage());
                }
            }
        }
     }
}
