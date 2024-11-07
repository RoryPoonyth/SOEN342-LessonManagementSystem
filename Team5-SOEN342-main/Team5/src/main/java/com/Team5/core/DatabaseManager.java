package com.Team5.core;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Scanner;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:Team5.db";
    private static final String SCHEMA_FILE = "/db_setup.sql";
    private static final String SEED_FILE = "/data_load.sql";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase(Scanner scanner) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                promptResetAndSeed(conn, scanner);
            }
        } catch (SQLException e) {
            System.err.println("An error occurred while initializing the database. Please try again. Error: " + e.getMessage());
        }
    }

    private static void promptResetAndSeed(Connection conn, Scanner scanner) {
        System.out.print("Would you like to reset and populate the database? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if ("yes".equals(response)) {
            resetAndSeedDatabase(conn);
        } else {
            System.out.println("Database setup is complete. No changes have been made.");
        }
    }

    private static void resetAndSeedDatabase(Connection conn) {
        try {
            System.out.println("Resetting the database, please wait...");
            executeSqlFile(conn, SCHEMA_FILE);
            System.out.println("Database reset successfully.");
            System.out.println("Loading initial data into the database, please wait...");
            executeSqlFile(conn, SEED_FILE);
            System.out.println("Database populating completed successfully.");
        } catch (Exception e) {
            System.err.println("An error occurred while resetting and populating the database. Please check the details and try again. Error: " + e.getMessage());
        }
    }

    private static void executeSqlFile(Connection conn, String filePath) {
        try (InputStream is = DatabaseManager.class.getResourceAsStream(filePath);
             Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
            scanner.useDelimiter(";");
            try (Statement stmt = conn.createStatement()) {
                while (scanner.hasNext()) {
                    String sqlStatement = scanner.next().trim();
                    if (!sqlStatement.isEmpty()) {
                        stmt.execute(sqlStatement);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while executing SQL file " + filePath + ". Please check the file and try again. Error: " + e.getMessage());
        }
    }
}
