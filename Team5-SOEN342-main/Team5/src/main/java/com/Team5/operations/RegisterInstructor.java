package com.Team5.operations;

import com.Team5.collections.InstructorCollection;

import java.util.Scanner;

public class RegisterInstructor implements Operation {
    private final Scanner scanner;

    public RegisterInstructor(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        String firstName = requestFirstName();
        String lastName = requestLastName();
        String email = requestEmail();
        String password = requestPassword();

        if (InstructorCollection.getByEmail(email) != null) {
            System.out.println("An instructor with this email already exists.");
            return;
        }

        boolean result = InstructorCollection.createInstructor(firstName, lastName, email, password);
        if (result) {
            System.out.println("Instructor registered successfully.");
        } else {
            System.out.println("Failed to register instructor.");
        }
    }

    private String requestFirstName() {
        System.out.println("Enter instructor's first name:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.matches("[A-Za-z]+")) {
                return input;
            }
            System.out.println("Invalid name. Please enter a valid first name without numbers.");
        }
    }

    private String requestLastName() {
        System.out.println("Enter instructor's last name:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.matches("[A-Za-z]+")) {
                return input;
            }
            System.out.println("Invalid name. Please enter a valid last name without numbers.");
        }
    }

    private String requestEmail() {
        System.out.println("Enter instructor's email:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                return input;
            }
            System.out.println("Invalid email format. Please enter a valid email.");
        }
    }

    private String requestPassword() {
        System.out.println("Enter instructor's password:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.length() >= 6) {
                return input;
            }
            System.out.println("Password must be at least 6 characters long.");
        }
    }
}
