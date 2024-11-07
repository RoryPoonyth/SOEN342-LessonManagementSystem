package com.Team5.collections;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.Team5.models.Instructor;
import com.Team5.core.DatabaseManager;

public class InstructorCollection {
    private static final String UPDATE_INSTRUCTOR_SQL = "UPDATE instructor SET firstName = ?, lastName = ?, email = ?, password = ? WHERE id = ?";
    private static final String INSERT_INSTRUCTOR_SQL = "INSERT INTO instructor (firstName, lastName, email, password, role) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_INSTRUCTOR_BY_ID_SQL = "SELECT * FROM instructor WHERE id = ?";
    private static final String SELECT_INSTRUCTOR_BY_EMAIL_SQL = "SELECT * FROM instructor WHERE email = ?";
    private static final String SELECT_ALL_INSTRUCTORS_SQL = "SELECT * FROM instructor";
    private static final String VALIDATE_CREDENTIALS_SQL = "SELECT * FROM instructor WHERE email = ? AND password = ?";
    private static final String DELETE_INSTRUCTOR_SQL = "DELETE FROM instructor WHERE id = ?";

    public static List<Instructor> getInstructors() {
        return executeQuery(SELECT_ALL_INSTRUCTORS_SQL, statement -> {}, InstructorCollection::extractInstructorList);
    }

    public static Instructor getById(Integer id) {
        return executeQuery(SELECT_INSTRUCTOR_BY_ID_SQL, statement -> statement.setInt(1, id), InstructorCollection::extractInstructor);
    }

    public static Instructor getByEmail(String email) {
        return executeQuery(SELECT_INSTRUCTOR_BY_EMAIL_SQL, statement -> statement.setString(1, email), InstructorCollection::extractInstructor);
    }

    public static boolean createInstructor(String firstName, String lastName, String email, String password) {
        Instructor newInstructor = new Instructor(firstName, lastName, email, password);
        return add(newInstructor);
    }

    public static boolean add(Instructor instructor) {
        return executeUpdate(INSERT_INSTRUCTOR_SQL, statement -> {
            statement.setString(1, instructor.getFirstName());
            statement.setString(2, instructor.getLastName());
            statement.setString(3, instructor.getEmail());
            statement.setString(4, instructor.getPassword());
            statement.setString(5, instructor.getRole().toString());
        }, generatedKeys -> {
            if (generatedKeys.next()) {
                instructor.setId(generatedKeys.getInt(1));
            }
        }) > 0;
    }

    public static Instructor validateCredentials(String email, String password) {
        return executeQuery(VALIDATE_CREDENTIALS_SQL, statement -> {
            statement.setString(1, email);
            statement.setString(2, password);
        }, InstructorCollection::extractInstructor);
    }

    public static boolean delete(Integer id) {
        return executeUpdate(DELETE_INSTRUCTOR_SQL, statement -> statement.setInt(1, id), null) > 0;
    }

    public static boolean update(Instructor instructor) {
        return executeUpdate(UPDATE_INSTRUCTOR_SQL, statement -> {
            statement.setString(1, instructor.getFirstName());
            statement.setString(2, instructor.getLastName());
            statement.setString(3, instructor.getEmail());
            statement.setString(4, instructor.getPassword());
            statement.setInt(5, instructor.getId());
        }, null) > 0;
    }

    private static List<Instructor> extractInstructorList(ResultSet resultSet) throws SQLException {
        List<Instructor> instructors = new ArrayList<>();
        while (resultSet.next()) {
            instructors.add(extractInstructorFromResultSet(resultSet));
        }
        return instructors;
    }

    private static Instructor extractInstructor(ResultSet resultSet) throws SQLException {
        return resultSet.next() ? extractInstructorFromResultSet(resultSet) : null;
    }

    private static Instructor extractInstructorFromResultSet(ResultSet resultSet) throws SQLException {
        Instructor instructor = new Instructor(
                resultSet.getString("firstName"),
                resultSet.getString("lastName"),
                resultSet.getString("email"),
                resultSet.getString("password"));
        instructor.setId(resultSet.getInt("id"));
        return instructor;
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
