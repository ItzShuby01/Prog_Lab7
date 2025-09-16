package org.example.server.commands;

import java.util.Optional;
import org.example.common.command.RemoveByIdCommand;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.common.util.Logger;
import org.example.server.manager.CollectionManager;

public class RemoveById implements ServerCommand {
  public static final String DESCRIPTION =
          "remove_by_id id: remove an element from a collection by its id";

  private final CollectionManager collectionManager;

  public RemoveById(CollectionManager collectionManager) {
    this.collectionManager = collectionManager;
  }

  public Response execute(RemoveByIdCommand commandDto) {
    String arg = commandDto.getArg();
    String username = commandDto.getUsername();

    if (arg == null || arg.trim().isEmpty()) {
      return new Response("Error: ID argument is missing for remove_by_id.", false);
    }

    String trimmedArg = arg.trim();

    // Server-side validation of ID format to allow only positive integers
    if (!trimmedArg.matches("[1-9]\\d*")) {
      return new Response("Invalid ID format: ID must be a positive integer.", false);
    }

    try {
      long id = Long.parseLong(trimmedArg);

      // Find the person by ID to get their name for the log
      Optional<Person> personToRemove = collectionManager.getById(id);

      boolean success = collectionManager.removePersonByIdAndUsername(id, username);

      if (success) {
        if (personToRemove.isPresent()) {
          Logger.info("Removed person '" + personToRemove.get().getName() + "' by user '" + username + "'.");
        } else {
          Logger.info("Removed person with ID '" + id + "' by user '" + username + "'.");
        }
        return new Response("Person with ID " + id + " removed successfully.", true);
      } else {
        // Return a specific error message if the person doesn't exist or is not owned by the user
        return new Response(
                "Failed to remove person with ID "
                        + id
                        + ". It may not exist or you do not have permission to delete it.",
                false);
      }
    } catch (NumberFormatException e) {
      return new Response("Invalid ID format: " + e.getMessage(), false);
    }
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}
