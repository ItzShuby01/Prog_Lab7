package org.example.server.commands;

import org.example.common.command.ShowCommand;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.server.manager.CollectionManager;


import java.util.Set;
import java.util.TreeSet;

public class Show implements ServerCommand{
    private static final String DESCRIPTION = "show: print all elements of the collection to standard output";
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

     //Takes a ShowCommand DTO and returns a Response DTO with the list of persons.
    public Response execute(ShowCommand commandDto) {

        // unmodifiable set from the collection manager
        Set<Person> unmodifiableSet = collectionManager.getCollection();

        // a new TreeSet from the unmodifiable set.
        TreeSet<Person> personCollection = new TreeSet<>(unmodifiableSet);
        if (personCollection.isEmpty()) {
            return new Response("Collection is empty.", true);
        }

        //a string representation to send to the client
        StringBuilder sb = new StringBuilder();
        sb.append("DISPLAYING THE COLLECTION DATA:\n");
        personCollection.forEach(person -> sb.append(person.toString()).append("\n"));

        return new Response(sb.toString(), true);
    }
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}