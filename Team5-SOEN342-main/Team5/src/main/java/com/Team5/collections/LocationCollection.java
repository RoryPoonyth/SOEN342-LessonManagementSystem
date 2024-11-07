package com.Team5.collections;

import com.Team5.models.Location;
import com.Team5.core.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationCollection {
    private static final String INSERT_LOCATION_SQL = "INSERT INTO location (name, address, city, province, postalCode) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_LOCATIONS_SQL = "SELECT * FROM location";
    private static final String SELECT_LOCATION_BY_ID_SQL = "SELECT * FROM location WHERE id = ?";
    private static final String DELETE_LOCATION_BY_ID_SQL = "DELETE FROM location WHERE id = ?";

    public static boolean add(Location location) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_LOCATION_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, location.getName());
            statement.setString(2, location.getAddress());
            statement.setString(3, location.getCity());
            statement.setString(4, location.getProvince());
            statement.setString(5, location.getPostalCode());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        location.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_LOCATIONS_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Location location = new Location(
                        resultSet.getString("name"),
                        resultSet.getString("address"),
                        resultSet.getString("city"),
                        resultSet.getString("province"),
                        resultSet.getString("postalCode"));
                location.setId(resultSet.getInt("id"));
                locations.add(location);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locations;
    }

    public static Location getById(Integer id) {
        Location location = null;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_LOCATION_BY_ID_SQL)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    location = new Location(
                            resultSet.getString("name"),
                            resultSet.getString("address"),
                            resultSet.getString("city"),
                            resultSet.getString("province"),
                            resultSet.getString("postalCode"));
                    location.setId(resultSet.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return location;
    }

    public static boolean delete(Integer id) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_LOCATION_BY_ID_SQL)) {

            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
