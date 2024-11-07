package com.Team5.operations;

import com.Team5.collections.LocationCollection;
import com.Team5.models.Administrator;
import com.Team5.models.Location;
import com.Team5.core.Console;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ManageLocations implements Operation {
    @SuppressWarnings("unused")
    private final Administrator admin;
    private final Scanner scanner;

    public ManageLocations(Administrator admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        while (true) {
            List<Location> allLocations = LocationCollection.getLocations();

            if (allLocations.isEmpty()) {
                System.out.println("No locations available.");
                return;
            }

            Console.printTable(allLocations, Arrays.asList("ID"));

            System.out.println("\nManage Locations:");
            System.out.println("1. Add a Location");
            System.out.println("2. Remove a Location");
            System.out.print("Enter your choice or type -1 to exit: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addLocation();
                    break;
                case "2":
                    removeLocation();
                    break;
                case "-1":
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
                    break;
            }
        }
    }

    private void addLocation() {
        System.out.print("\nEnter the name of the new location: ");
        String name = scanner.nextLine();
        System.out.print("Enter the address: ");
        String address = scanner.nextLine();
        System.out.print("Enter the city: ");
        String city = scanner.nextLine();
        System.out.print("Enter the province: ");
        String province = scanner.nextLine();
        System.out.print("Enter the postal code: ");
        String postalCode = scanner.nextLine();

        Location location = new Location(name, address, city, province, postalCode);
        boolean isAdded = LocationCollection.add(location);

        if (isAdded) {
            System.out.println("\nLocation \"" + name + "\" added successfully.");
        } else {
            System.out.println("\nFailed to add location. Please try again.");
        }
    }

    private void removeLocation() {
        System.out.print("\nEnter the ID of the location to remove: ");
        try {
            int locationId = Integer.parseInt(scanner.nextLine());
            boolean isRemoved = LocationCollection.delete(locationId);

            if (isRemoved) {
                System.out.println("\nLocation with ID " + locationId + " removed successfully.");
            } else {
                System.out.println("\nFailed to remove location. Please ensure the ID is correct and try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input. Please enter a valid numeric ID.");
        }
    }
}
