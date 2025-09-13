package org.example.server.commands;

import org.example.common.command.AverageOfHeightCommand;
import org.example.common.response.Response;
import org.example.server.manager.CollectionManager;

import java.sql.SQLException;

public class AverageOfHeight implements ServerCommand{
    public static final String DESCRIPTION = "average_of_height: output the average height field value for all elements in a collection";
    private final CollectionManager collectionManager;

    public AverageOfHeight(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }


    // Takes an AverageOfHeightCommand DTO and returns a Response DTO.
    public Response execute(AverageOfHeightCommand commandDto) throws SQLException {
        try {
            double average = collectionManager.getAverageOfHeight();
            return new Response(String.format("Average height: %.2f", average), true);
        } catch (IllegalStateException e) {
            return new Response(e.getMessage(), false);
        } catch (SQLException e){
            return new Response("Error loading from database", false);
        }
    }
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}