package com.Team5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.Team5.collections.ClientCollection;
import com.Team5.collections.InstructorCollection;
import com.Team5.enums.UserType;
import com.Team5.models.Administrator;
import com.Team5.models.Client;
import com.Team5.models.Instructor;
import com.Team5.op_handlers.Command;
import com.Team5.op_handlers.Registration;
import com.Team5.operations.Operation;
import com.Team5.core.Console;
import com.Team5.core.DatabaseManager;

public class Main {
    private static boolean loggedIn = false;
    private static boolean applicationRunning = true;
    private static Object user;
    private static boolean debugMode = false;

    public static void main(String[] args) {
        run(true);
    }

    public static void run(boolean isDebugMode) {
        Scanner scanner = new Scanner(System.in);

        DatabaseManager.initializeDatabase(scanner);

        debugMode = isDebugMode;

        System.out.println("Welcome to the System! Please choose one of the options below to continue.");

        while (applicationRunning) {
            loggedIn = false;
            if (!debugMode) {
                Console.clearConsole();
            }

            System.out.println("\nPlease choose an option:");
            System.out.println("1. Log in to your account");
            System.out.println("2. Register a new account");
            System.out.println("3. Exit the application");

            System.out.print("Enter the number of your choice: ");
            String input = scanner.nextLine();

            int option;
            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("\nOops! That doesn't seem like a valid number. Please try again by entering a number from the options.");
                continue;
            }

            if (option == 1) {
                while (!loggedIn) {
                    logIn(scanner);
                }
            } else if (option == 2) {
                runCommandLoop(Registration.getRegistrationCommands(scanner), scanner);
            } else if (option == 3) {
                System.out.println("\nThank you for using our system. Have a wonderful day!");
                System.exit(0);
            } else {
                System.out.println("\nInvalid selection. Please enter a number between 1 and 3.");
            }
        }

        scanner.close();
    }

    private static void logIn(Scanner scanner) {
        System.out.println("\nPlease select your user type:");
        UserType[] userTypes = UserType.values();
        for (int i = 0; i < userTypes.length; i++) {
            System.out.println((i + 1) + ". " + userTypes[i]);
        }

        System.out.print("Enter the number of your user type or -1 to cancel: ");
        String userTypeInput = scanner.nextLine();

        int userTypeIndex;
        try {
            userTypeIndex = Integer.parseInt(userTypeInput) - 1;
            if (userTypeIndex == -2) {
                loggedIn = true;
                return;
            }
            if (userTypeIndex < 0 || userTypeIndex >= userTypes.length) {
                System.out.println("\nInvalid selection. Please try again by entering a valid number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("\nOops! That doesn't seem like a valid number. Please try again.");
            return;
        }

        UserType userType = userTypes[userTypeIndex];

        System.out.print("\nEnter your email address: ");
        String email = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (userType == UserType.CLIENT) {
            Client client = ClientCollection.validateCredentials(email, password);
            if (client != null) {
                user = client;
                loggedIn = true;
            } else {
                System.out.println("\nInvalid credentials for a client account. Please double-check your email and password, and try again.");
            }
        } else if (userType == UserType.INSTRUCTOR) {
            Instructor instructor = InstructorCollection.validateCredentials(email, password);
            if (instructor != null) {
                user = instructor;
                loggedIn = true;
            } else {
                System.out.println("\nInvalid credentials for an instructor account. Please double-check your email and password, and try again.");
            }
        } else if (userType == UserType.ADMINISTRATOR) {
            Administrator admin = Administrator.getInstance();
            if (email.equals(admin.getEmail()) && password.equals(admin.getPassword())) {
                user = admin;
                loggedIn = true;
            } else {
                System.out.println("\nInvalid credentials for an administrator account. Please double-check your email and password, and try again.");
            }
        }

        if (loggedIn) {
            System.out.println("\nLogin successful! Welcome, " + userType + ".");
            Map<String, Operation> userCommands = Command.getCommands(userType, user, scanner);
            runCommandLoop(userCommands, scanner);
            System.out.println("\nYou have successfully logged out. Have a great day!");
        }
    }

    private static void runCommandLoop(Map<String, Operation> commandsMap, Scanner scanner) {
        List<String> commandNames = new ArrayList<>(commandsMap.keySet());
        List<Operation> commands = new ArrayList<>(commandsMap.values());

        while (true) {
            if (!debugMode) {
                Console.clearConsole();
            }

            System.out.println("\nHere are the available commands:");
            for (int i = 0; i < commandNames.size(); i++) {
                System.out.println((i + 1) + ". " + commandNames.get(i));
            }

            System.out.print("\nEnter the number of the command you'd like to execute or -1 to go back: ");
            String input = scanner.nextLine();

            try {
                int commandIndex = Integer.parseInt(input) - 1;
                if (commandIndex >= 0 && commandIndex < commands.size()) {
                    Operation command = commands.get(commandIndex);
                    if (!debugMode) {
                        Console.clearConsole();
                    }
                    command.execute();

                    if ("Log Out".equalsIgnoreCase(commandNames.get(commandIndex))) {
                        break;
                    }
                } else if (commandIndex == -2) {
                    break;
                } else {
                    System.out.println("\nInvalid command number. Please try again by selecting a valid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nPlease enter a valid number to proceed.");
            }
            System.out.println();
        }
    }
}
