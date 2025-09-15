package org.example.server.commands;

import org.example.common.command.AddCommand;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.server.manager.CollectionManager;
import org.example.common.util.Logger;

/**
 * Handles the 'add' command, adding a new element to the collection.
 */
public class Add implements ServerCommand {
  public static final String DESCRIPTION = "add {element}: adds a new element to the collection";

  private final CollectionManager collectionManager;

  public Add(CollectionManager collectionManager) {
    this.collectionManager = collectionManager;
  }


  public Response execute(AddCommand commandDto) {
    Person person = commandDto.getPerson();
    String username = commandDto.getUsername();

    if (person == null) {
      return new Response("Error: Person object is missing from the command.", false);
    }

    // This is the crucial fix: pass the username to the collection manager
    // so it can be saved with the new person.
    if (collectionManager.addPerson(person, username)) {
      Logger.info("Added new person: " + person.getName() + " by user: " + username);
      return new Response("Person '" + person.getName() + "' added successfully.", true);
    } else {
      return new Response("Failed to add person: " + person.getName() + ". Possible internal error.", false);
    }
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}
