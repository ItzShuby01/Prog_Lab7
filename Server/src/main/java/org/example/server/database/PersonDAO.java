package org.example.server.database;

import org.example.common.data.Person;

import java.sql.SQLException;
import java.util.Set;

 //  Interface for a Data Access Object (DAO) for the Person class.
 // This defines the contract for database operations.

public interface PersonDAO {

    // Retrieves all Person objects from the database \ return a Set of all Person objects.
    Set<Person> getAll() throws SQLException;


    //Adds a new Person object to the database.
    boolean add(Person person) throws SQLException;


    // Updates an existing Person object in the database.
    boolean update(long id, Person updatedPerson) throws SQLException;


    // Deletes a Person object from the database by its ID.
    boolean delete(long id) throws SQLException;


    // Deletes all Person objects from the database.
    boolean clear() throws SQLException;

}
