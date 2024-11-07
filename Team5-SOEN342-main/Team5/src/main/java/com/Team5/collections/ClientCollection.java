package com.Team5.collections;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.Team5.models.Client;
import com.Team5.core.DatabaseManager;

public class ClientCollection {
    private static final String INSERT_CLIENT_SQL = "INSERT INTO client (firstName, lastName, email, password, role) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_CLIENT_BY_ID_SQL = "SELECT * FROM client WHERE id = ?";
    private static final String SELECT_CLIENT_BY_EMAIL_SQL = "SELECT * FROM client WHERE email = ?";
    private static final String SELECT_ALL_CLIENTS_SQL = "SELECT * FROM client";
    private static final String VALIDATE_CREDENTIALS_SQL = "SELECT * FROM client WHERE email = ? AND password = ?";
    private static final String DELETE_CLIENT_SQL = "DELETE FROM client WHERE id = ?";

    public static List<Client> getClients() {
        return executeQuery(SELECT_ALL_CLIENTS_SQL, statement -> {}, ClientCollection::extractClientList);
    }

    public static Client getById(Integer id) {
        return executeQuery(SELECT_CLIENT_BY_ID_SQL, statement -> statement.setInt(1, id), ClientCollection::extractClient);
    }

    public static Client getByEmail(String email) {
        return executeQuery(SELECT_CLIENT_BY_EMAIL_SQL, statement -> statement.setString(1, email), ClientCollection::extractClient);
    }

    public static boolean createClient(String firstName, String lastName, String email, String password) {
        Client newClient = new Client(firstName, lastName, email, password);
        return add(newClient);
    }

    public static boolean add(Client client) {
        return executeUpdate(INSERT_CLIENT_SQL, statement -> {
            statement.setString(1, client.getFirstName());
            statement.setString(2, client.getLastName());
            statement.setString(3, client.getEmail());
            statement.setString(4, client.getPassword());
            statement.setString(5, client.getRole().toString());
        }, generatedKeys -> {
            if (generatedKeys.next()) {
                client.setId(generatedKeys.getInt(1));
            }
        }) > 0;
    }

    public static Client validateCredentials(String email, String password) {
        return executeQuery(VALIDATE_CREDENTIALS_SQL, statement -> {
            statement.setString(1, email);
            statement.setString(2, password);
        }, ClientCollection::extractClient);
    }

    public static boolean delete(Integer id) {
        return executeUpdate(DELETE_CLIENT_SQL, statement -> statement.setInt(1, id), null) > 0;
    }

    private static List<Client> extractClientList(ResultSet resultSet) throws SQLException {
        List<Client> clients = new ArrayList<>();
        while (resultSet.next()) {
            clients.add(extractClientFromResultSet(resultSet));
        }
        return clients;
    }

    private static Client extractClient(ResultSet resultSet) throws SQLException {
        return resultSet.next() ? extractClientFromResultSet(resultSet) : null;
    }

    private static Client extractClientFromResultSet(ResultSet resultSet) throws SQLException {
        Client client = new Client(
                resultSet.getString("firstName"),
                resultSet.getString("lastName"),
                resultSet.getString("email"),
                resultSet.getString("password"));
        client.setId(resultSet.getInt("id"));
        return client;
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
