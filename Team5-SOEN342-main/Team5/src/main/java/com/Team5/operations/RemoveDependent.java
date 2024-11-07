package com.Team5.operations;

import com.Team5.collections.ChildCollection;
import com.Team5.models.Child;
import com.Team5.models.Client;

import java.util.List;
import java.util.Scanner;

public class RemoveDependent implements Operation {
    private final Client client;
    private final Scanner scanner;

    public RemoveDependent(Client client, Scanner scanner) {
        this.client = client;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        List<Child> dependents = ChildCollection.getChildrenByClientId(client.getId());

        if (dependents.isEmpty()) {
            System.out.println("You have no dependents to remove.");
            return;
        }

        System.out.println("Your dependents:");
        for (int i = 0; i < dependents.size(); i++) {
            Child dependent = dependents.get(i);
            System.out.printf("%d. %s %s (Date of Birth: %s)%n", i + 1, dependent.getFirstName(), dependent.getLastName(), dependent.getDateOfBirth());
        }

        System.out.print("Enter the number of the dependent you wish to remove or type -1 to cancel: ");
        int selectedIndex = requestDependentIndex(dependents.size());
        if (selectedIndex == -1) {
            System.out.println("Operation cancelled.");
            return;
        }

        Child selectedDependent = dependents.get(selectedIndex);

        if (ChildCollection.deleteChild(selectedDependent.getId())) {
            System.out.println("Dependent removed successfully.");
        } else {
            System.out.println("Failed to remove dependent. Please try again.");
        }
    }

    private int requestDependentIndex(int numberOfDependents) {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
                if (input == -1) {
                    return -1;
                } else if (input > 0 && input <= numberOfDependents) {
                    return input - 1;
                } else {
                    System.out.println("Invalid selection. Please enter a valid number.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); // Consume the invalid input
            }
        }
    }
}