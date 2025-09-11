package org.example.server.database;

import org.example.common.data.*;
import org.example.server.util.DatabaseConnectionManager;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

 // A concrete implementation of the PersonDAO interface for PostgreSQL.
 // This class handles all SQL operations for the Person objects.

public class PersonDAOPostgreSQL implements PersonDAO {
    private final DatabaseConnectionManager connectionManager;

    public PersonDAOPostgreSQL(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Set<Person> getAll() throws SQLException {
        Set<Person> persons = new TreeSet<>();
        String sql = "SELECT * FROM persons ORDER BY id ASC";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                Coordinates coordinates = new Coordinates(
                        rs.getInt("coordinates_x"),
                        rs.getDouble("coordinates_y")
                );
                LocalDateTime creationDate = rs.getTimestamp("creation_date").toLocalDateTime();
                double height = rs.getDouble("height");
                EyeColor eyeColor = EyeColor.valueOf(rs.getString("eye_color"));
                HairColor hairColor = HairColor.valueOf(rs.getString("hair_color"));
                Country nationality = rs.getString("nationality") != null ? Country.valueOf(rs.getString("nationality")) : null;

                // Handle location
                Location location = null;
                if (rs.getObject("location_x") != null) {
                    location = new Location(
                            rs.getFloat("location_x"),
                            rs.getFloat("location_y"),
                            rs.getString("location_name")
                    );
                }

                Person person = new Person(
                        (int)id,
                        name,
                        coordinates,
                        creationDate,
                        height,
                        eyeColor,
                        hairColor,
                        nationality,
                        location
                );
                persons.add(person);
            }
        }
        return persons;
    }


    @Override
    public boolean add(Person person) throws SQLException {
        String sql = "INSERT INTO persons (name, coordinates_x, coordinates_y, height, eye_color, hair_color, nationality, location_x, location_y, location_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, person.getName());
            statement.setInt(2, person.getCoordinates().getX());
            statement.setDouble(3, person.getCoordinates().getY());
            statement.setDouble(4, person.getHeight());
            statement.setString(5, person.getEyeColor().name());
            statement.setString(6, person.getHairColor().name());
            if (person.getNationality() != null) {
                statement.setString(7, person.getNationality().name());
            } else {
                statement.setNull(7, Types.VARCHAR);
            }

            if (person.getLocation() != null) {
                statement.setFloat(8, person.getLocation().getX());
                statement.setFloat(9, person.getLocation().getY());
                statement.setString(10, person.getLocation().getName());
            } else {
                statement.setNull(8, Types.REAL);
                statement.setNull(9, Types.REAL);
                statement.setNull(10, Types.VARCHAR);
            }

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        }
    }

    @Override
    public boolean update(long id, Person updatedPerson) throws SQLException {
        String sql = "UPDATE persons SET name = ?, coordinates_x = ?, coordinates_y = ?, height = ?, eye_color = ?, hair_color = ?, nationality = ?, location_x = ?, location_y = ?, location_name = ? WHERE id = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, updatedPerson.getName());
            statement.setInt(2, updatedPerson.getCoordinates().getX());
            statement.setDouble(3, updatedPerson.getCoordinates().getY());
            statement.setDouble(4, updatedPerson.getHeight());
            statement.setString(5, updatedPerson.getEyeColor().name());
            statement.setString(6, updatedPerson.getHairColor().name());

            if (updatedPerson.getNationality() != null) {
                statement.setString(7, updatedPerson.getNationality().name());
            } else {
                statement.setNull(7, Types.VARCHAR);
            }

            if (updatedPerson.getLocation() != null) {
                statement.setFloat(8, updatedPerson.getLocation().getX());
                statement.setFloat(9, updatedPerson.getLocation().getY());
                statement.setString(10, updatedPerson.getLocation().getName());
            } else {
                statement.setNull(8, Types.REAL);
                statement.setNull(9, Types.REAL);
                statement.setNull(10, Types.VARCHAR);
            }
            statement.setLong(11, id);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM persons WHERE id = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setLong(1, id);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean clear() throws SQLException {
        String sql = "DELETE * FROM persons";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
            return true;
        }
    }
    public int removeLower(Person person) throws SQLException {
        String sql = "DELETE FROM persons WHERE id < ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, person.getId());
            return pstmt.executeUpdate();
        }
    }

    public double getAverageHeight() throws SQLException {
        String sql = "SELECT AVG(height) FROM persons";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
    // Get max height directly from the database
    public Optional<Double> getMaxHeight() throws SQLException {
        String sql = "SELECT MAX(height) FROM persons";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                double maxHeight = rs.getDouble(1);
                // The MAX() function can return null if the table is empty.
                // rs.wasNull() checks if the last value read was SQL NULL.
                if (rs.wasNull()) {
                    return Optional.empty();
                }
                return Optional.of(maxHeight);
            }
        }
        return Optional.empty();
    }


    private Person createPersonFromResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        Coordinates coordinates = new Coordinates(rs.getInt("coordinates_x"), rs.getDouble("coordinates_y"));
        LocalDateTime creationDate = rs.getTimestamp("creation_date").toLocalDateTime();
        double height = rs.getDouble("height");
        EyeColor eyeColor = EyeColor.valueOf(rs.getString("eye_color"));
        HairColor hairColor = HairColor.valueOf(rs.getString("hair_color"));
        Country nationality = Country.valueOf(rs.getString("nationality"));
        Location location = new Location(rs.getFloat("location_x"), rs.getFloat("location_y"), rs.getString("location_name"));

        return new Person((int) id, name, coordinates, creationDate, height, eyeColor, hairColor, nationality, location);
    }

}
