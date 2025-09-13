package org.example.server.manager;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.example.common.data.Location;
import org.example.common.data.Person;
import org.example.common.util.Logger;
import org.example.server.database.PersonDAOPostgreSQL;

public class CollectionManager {
  private final PersonDAOPostgreSQL personDAO;
  private Set<Person> personCollection;
  private final LocalDateTime creationDate;

  public CollectionManager(PersonDAOPostgreSQL personDAO) {
    this.personDAO = personDAO;
    this.personCollection = new TreeSet<>();
    this.creationDate = LocalDateTime.now();
    loadCollectionFromDatabase();
  }

  public double getAverageOfHeight() throws SQLException {
    return personDAO.getAverageHeight();
  }

  private void loadCollectionFromDatabase() {
    try {
      this.personCollection = personDAO.getAll();
      Logger.info("Loaded " + personCollection.size() + " persons from the database.");
    } catch (SQLException e) {
      Logger.error("Failed to load collection from database: " + e.getMessage());
      this.personCollection = new TreeSet<>();
    }
  }

  // All methods below now  use the PersonDAOPostgreSQL class
  public Set<Person> getCollection() {
    return Collections.unmodifiableSet(personCollection);
  }

  public String getCollectionType() {
    return personCollection.getClass().getName();
  }

  public int getSize() {
    return personCollection.size();
  }

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public boolean addPerson(Person person) {
    try {
      boolean success = personDAO.add(person);
      if (success) {
        loadCollectionFromDatabase(); // Reload after adding
      }
      return success;
    } catch (SQLException e) {
      Logger.error("Failed to add person to database: " + e.getMessage());
      return false;
    }
  }

  public boolean removePerson(Person person) {
    try {
      boolean success = personDAO.delete(person.getId());
      if (success) {
        loadCollectionFromDatabase(); // Reload after removing
      }
      return success;
    } catch (SQLException e) {
      Logger.error("Failed to remove person from database: " + e.getMessage());
      return false;
    }
  }

  public void clear() {
    try {
      if (personDAO.clear()) {
        personCollection.clear();
        Logger.info("Collection cleared successfully.");
      } else {
        Logger.warn("Database clear operation failed.");
      }
    } catch (SQLException e) {
      Logger.error("Error clearing collection: " + e.getMessage());
    }
  }

  public Optional<Person> getMaxById() {
    return personCollection.stream().max(Person::compareTo);
  }

  public long countByLocation(Location targetLocation) {
    if (targetLocation == null) {
      return 0;
    }
    return personCollection.stream()
        .filter(p -> p.getLocation() != null && targetLocation.equals(p.getLocation()))
        .count();
  }

  public Optional<Person> getById(long id) {
    return personCollection.stream().filter(p -> p.getId() != null && p.getId() == id).findFirst();
  }

  public boolean updatePerson(long id, Person updatedPerson) {
    try {
      boolean success = personDAO.update(id, updatedPerson);
      if (success) {
        loadCollectionFromDatabase(); // Reload after updating
      }
      return success;
    } catch (SQLException e) {
      Logger.error("Failed to update person in database: " + e.getMessage());
      return false;
    }
  }

  public int removeLower(Person threshold) {
    try {
      int rowsAffected = personDAO.removeLower(threshold);
      if (rowsAffected > 0) {
        loadCollectionFromDatabase(); // Reload after removing
      }
      return rowsAffected;
    } catch (SQLException e) {
      Logger.error("Failed to remove lower elements from database: " + e.getMessage());
      return 0;
    }
  }

  public Optional<Double> getMaxHeight() throws SQLException {
    return personDAO.getMaxHeight();
  }

  public boolean removePersonByIdAndUsername(long id, String username) {
    try {
      boolean success = personDAO.removeByIdAndUsername(id, username);
      if (success) {
        loadCollectionFromDatabase();
      }
      return success;
    } catch (SQLException e) {
      Logger.error("Failed to remove person from database: " + e.getMessage());
      return false;
    }
  }
}
