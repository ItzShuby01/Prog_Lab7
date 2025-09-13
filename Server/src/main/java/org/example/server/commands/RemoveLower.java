package org.example.server.commands;

import org.example.common.command.RemoveLowerCommand;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.common.util.ValidationUtil;
import org.example.server.manager.CollectionManager;

public class RemoveLower implements ServerCommand {
    public static final String DESCRIPTION = "remove_lower {element}: remove all elements from the collection that are less than the specified number";

    private final CollectionManager collectionManager;

    public RemoveLower(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Response execute(RemoveLowerCommand commandDto) {
        Person thresholdPerson = commandDto.getPerson();

        if (thresholdPerson == null) {
            return new Response("Error: Person object for comparison is missing from the command.", false);
        }

        String validationError = ValidationUtil.validatePerson(thresholdPerson);
        if (validationError != null) {
            return new Response("Validation Error for threshold person: " + validationError, false);
        }

        collectionManager.removeLower(thresholdPerson);
        return new Response("Remove lower operation completed successfully.", true);
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
