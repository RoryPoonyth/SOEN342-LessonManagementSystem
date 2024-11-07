package com.Team5.operations;

import com.Team5.collections.ClientCollection;
import com.Team5.collections.InstructorCollection;
import com.Team5.enums.UserType;
import com.Team5.models.Administrator;
import com.Team5.models.Client;
import com.Team5.models.Instructor;
import com.Team5.core.Console;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DeleteUser implements Operation {
    @SuppressWarnings("unused")
	private final Administrator admin;
    private final Scanner scanner;

    public DeleteUser(Administrator admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        UserType userType = requestUserType();
        if (userType == null) return;

        switch (userType) {
            case CLIENT:
                deleteClient();
                break;
            case INSTRUCTOR:
                deleteInstructor();
                break;
            default:
                System.out.println("Invalid user type.");
                break;
        }
    }

    private void deleteClient() {
        List<Client> clients = ClientCollection.getClients();
        if (clients.isEmpty()) {
            System.out.println("No clients available to delete.");
            return;
        }

        Console.printTable(clients, Arrays.asList("Id", "Password", "Role", "Children", "Booking"));
        int userId = requestUserId(clients);
        if (userId == -1) return;

        if (confirmDeletion()) {
            ClientCollection.delete(userId);
            System.out.println("Client with ID " + userId + " has been deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void deleteInstructor() {
        List<Instructor> instructors = InstructorCollection.getInstructors();
        if (instructors.isEmpty()) {
            System.out.println("No instructors available to delete.");
            return;
        }

        Console.printTable(instructors, Arrays.asList("Password", "Role", "Lessons"));
        int userId = requestUserId(instructors);
        if (userId == -1) return;

        if (confirmDeletion()) {
            InstructorCollection.delete(userId);
            System.out.println("Instructor with ID " + userId + " has been deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private UserType requestUserType() {
        System.out.println("Would you like to delete a CLIENT or INSTRUCTOR?");
        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                UserType userType = UserType.valueOf(input);
                if (userType == UserType.CLIENT || userType == UserType.INSTRUCTOR) {
                    return userType;
                } else {
                    System.out.println("Only CLIENT or INSTRUCTOR can be deleted. Try again.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input. Please enter CLIENT or INSTRUCTOR.");
            }
        }
    }

    private Integer requestUserId(List<?> users) {
        System.out.println("Please enter the ID of the user you wish to delete: ");
        while (true) {
            try {
                int userId = Integer.parseInt(scanner.nextLine());
                for (Object user : users) {
                    if ((user instanceof Client && ((Client) user).getId() == userId) ||
                            (user instanceof Instructor && ((Instructor) user).getId() == userId)) {
                        return userId;
                    }
                }
                System.out.println("Invalid ID. Please enter a valid user ID from the list.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric ID.");
            }
        }
    }

    private boolean confirmDeletion() {
        System.out.println("Are you sure you want to delete this user? This action is irreversible. (yes/no)");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        return confirmation.equals("yes");
    }
}
