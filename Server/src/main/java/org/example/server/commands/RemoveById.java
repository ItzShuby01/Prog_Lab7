package org.example.server.commands;

import org.example.common.command.RemoveByIdCommand;
import org.example.common.response.Response;
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

      // Call the Secure method on the CollectionManager that checks the user
      boolean success = collectionManager.removePersonByIdAndUsername(id, username);

      if (success) {
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
