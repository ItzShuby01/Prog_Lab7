package org.example.server.commands;

import org.example.common.command.AddCommand;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.server.manager.CollectionManager;
import org.example.common.util.Logger;


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

    // Add to the database and reload the collection.
    if (collectionManager.addPerson(person, username)) {
      Logger.info("Added new person '" + person.getName() + "' by user: '" + username + "'.");
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
