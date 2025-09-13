package org.example.server.commands;

import org.example.common.command.AddIfMaxCommand;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.common.util.Logger;
import org.example.server.manager.CollectionManager;
import java.sql.SQLException;
import java.util.Optional;

public class AddIfMax implements ServerCommand {
    public static final String DESCRIPTION = "add_if_max: add a new person to the collection if its height is greater than the height of the maximum person";
    private final CollectionManager collectionManager;

    public AddIfMax(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Response execute(AddIfMaxCommand commandDto) {
        try {
            Person newPerson = commandDto.getPerson();
            Optional<Double> currentMaxHeightOptional = collectionManager.getMaxHeight();

            if (currentMaxHeightOptional.isPresent()) {
                double currentMaxHeight = currentMaxHeightOptional.get();
                if (newPerson.getHeight() > currentMaxHeight) {
                    collectionManager.addPerson(newPerson);
                    return new Response("Person added successfully, as its height > the current maximum.", true);
                } else {
                    return new Response("Person not added. Its height ≤ the current maximum.", false);
                }
            } else {
                // If the collection is empty, any person can be added
                collectionManager.addPerson(newPerson);
                return new Response("Collection was empty. Person added successfully.", true);
            }
        } catch (SQLException e) {
            Logger.error("Database error during add_if_max: " + e.getMessage());
            return new Response("Database error: " + e.getMessage(), false);
        }
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
