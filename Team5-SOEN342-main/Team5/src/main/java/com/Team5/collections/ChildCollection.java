package com.Team5.collections;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.Team5.models.Child;
import com.Team5.core.DatabaseManager;

public class ChildCollection {
    private static final String INSERT_CHILD_SQL = "INSERT INTO child (firstName, lastName, dateOfBirth, parentId) VALUES (?, ?, ?, ?)";
    private static final String SELECT_CHILD_BY_ID_SQL = "SELECT * FROM child WHERE id = ?";
    private static final String SELECT_CHILDREN_BY_CLIENT_ID_SQL = "SELECT * FROM child WHERE parentId = ?";
    private static final String VALIDATE_CHILD_SQL = "SELECT * FROM child WHERE parentId = ? AND firstName = ? AND lastName = ? AND dateOfBirth = ?";
    private static final String SELECT_ALL_CHILDREN_SQL = "SELECT * FROM child";
    private static final String DELETE_CHILD_SQL = "DELETE FROM child WHERE id = ?";
    

    public static List<Child> getChildren() {
        return executeQuery(SELECT_ALL_CHILDREN_SQL, statement -> {}, ChildCollection::extractChildList);
    }

    public static Child getById(int id) {
        return executeQuery(SELECT_CHILD_BY_ID_SQL, statement -> statement.setInt(1, id), ChildCollection::extractChild);
    }

    public static boolean add(Child child) {
        return executeUpdate(INSERT_CHILD_SQL, statement -> {
            statement.setString(1, child.getFirstName());
            statement.setString(2, child.getLastName());
            statement.setDate(3, Date.valueOf(child.getDateOfBirth()));
            statement.setInt(4, child.getParentId());
        }, generatedKeys -> {
            if (generatedKeys.next()) {
                child.setId(generatedKeys.getInt(1));
            }
        }) > 0;
    }

    public static boolean validateChild(Integer clientId, String firstName, String lastName, LocalDate dateOfBirth) {
        return executeQuery(VALIDATE_CHILD_SQL, statement -> {
            statement.setInt(1, clientId);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setDate(4, Date.valueOf(dateOfBirth));
        }, ResultSet::next);
    }

    public static Integer createChild(Integer clientId, String firstName, String lastName, LocalDate dateOfBirth) {
        Child newChild = new Child(firstName, lastName, dateOfBirth, clientId);
        return add(newChild) ? newChild.getId() : null;
    }

    public static List<Child> getChildrenByClientId(Integer clientId) {
        return executeQuery(SELECT_CHILDREN_BY_CLIENT_ID_SQL, statement -> statement.setInt(1, clientId), ChildCollection::extractChildList);
    }
    
    public static boolean deleteChild(int childId) {
        return executeUpdate(DELETE_CHILD_SQL, statement -> {
            statement.setInt(1, childId);
        }, null) > 0;
    }

    private static List<Child> extractChildList(ResultSet resultSet) throws SQLException {
        List<Child> children = new ArrayList<>();
        while (resultSet.next()) {
            children.add(extractChildFromResultSet(resultSet));
        }
        return children;
    }

    private static Child extractChild(ResultSet resultSet) throws SQLException {
        return resultSet.next() ? extractChildFromResultSet(resultSet) : null;
    }

    private static Child extractChildFromResultSet(ResultSet resultSet) throws SQLException {
        Child child = new Child(
                resultSet.getString("firstName"),
                resultSet.getString("lastName"),
                resultSet.getDate("dateOfBirth").toLocalDate(),
                resultSet.getInt("parentId"));
        child.setId(resultSet.getInt("id"));
        return child;
    }

    private static int executeUpdate(String sql, StatementPreparer preparer, GeneratedKeyHandler keyHandler) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparer.prepare(statement);
            int rowsAffected = statement.executeUpdate();
            if (keyHandler != null) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    keyHandler.handle(generatedKeys);
                }
            }
            return rowsAffected;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static <T> T executeQuery(String sql, StatementPreparer preparer, ResultSetHandler<T> handler) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            preparer.prepare(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                return handler.handle(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FunctionalInterface
    private interface StatementPreparer {
        void prepare(PreparedStatement statement) throws SQLException;
    }

    @FunctionalInterface
    private interface GeneratedKeyHandler {
        void handle(ResultSet generatedKeys) throws SQLException;
    }

    @FunctionalInterface
    private interface ResultSetHandler<T> {
        T handle(ResultSet resultSet) throws SQLException;
    }
}
