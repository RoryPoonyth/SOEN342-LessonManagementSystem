package com.Team5.operations;

import com.Team5.collections.BookingCollection;
import com.Team5.collections.ChildCollection;
import com.Team5.collections.LessonCollection;
import com.Team5.collections.LocationCollection;
import com.Team5.models.Child;
import com.Team5.models.Client;
import com.Team5.models.Lesson;
import com.Team5.models.Location;
import com.Team5.core.Console;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CreateBooking implements Operation {
    private final Client client;
    private final Scanner scanner;

    public CreateBooking(Client client, Scanner scanner) {
        this.client = client;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        List<Location> locations = LocationCollection.getLocations();
        Console.printTable(locations, Arrays.asList("Lessons"));

        System.out.print("Enter the location ID you wish to view available lessons for: ");
        int locationId = getValidLocationId(locations);

        List<Lesson> availableLessons = LessonCollection.getAvailableLessons(locationId);
        Console.printTable(availableLessons, Arrays.asList("Location Id", "Is Available", "Booking", "Assigned Instructor Id", "Id"));

        if (availableLessons.isEmpty()) {
            System.out.println("No available lessons found for the selected location.");
            return;
        }

        Integer lessonId = selectLesson(availableLessons);
        if (lessonId == null) {
            System.out.println("Invalid selection. Booking process aborted.");
            return;
        }

        List<Child> children = ChildCollection.getChildrenByClientId(client.getId());
        Integer childId = null;

        if (children != null && !children.isEmpty()) {
            System.out.print("Is this booking for an underage dependent? (yes/no): ");
            String response = scanner.next().trim().toLowerCase();

            if (response.equals("yes")) {
                Console.printTable(children, Arrays.asList("Id", "Parent Id"));
                System.out.print("Enter the child ID you wish to book for: ");
                childId = getValidChildId(children);
                if (childId == null) {
                    System.out.println("Invalid child ID. Booking process aborted.");
                    return;
                }
            }
        }

        if (BookingCollection.validateBooking(lessonId)) {
            BookingCollection.createBooking(client.getId(), lessonId, childId);
            System.out.println("Booking created successfully!");
        } else {
            System.out.println("Booking validation failed.");
        }
    }

    private Integer selectLesson(List<Lesson> lessons) {
        System.out.print("Select a lesson by ID: ");
        while (true) {
            try {
                int selection = scanner.nextInt();
                for (Lesson lesson : lessons) {
                    if (lesson.getId() == selection) {
                        return selection;
                    }
                }
                System.out.println("Invalid selection. Please enter a valid lesson ID.");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.next();
            }
        }
    }

    private int getValidLocationId(List<Location> locations) {
        System.out.print("Please enter a valid location ID (integer): ");
        while (true) {
            try {
                int locationId = scanner.nextInt();
                for (Location location : locations) {
                    if (location.getId() == locationId) {
                        return locationId;
                    }
                }
                System.out.println("Location ID does not exist. Please enter a valid location ID.");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.next();
            }
        }
    }

    private Integer getValidChildId(List<Child> children) {
        while (true) {
            try {
                int enteredId = scanner.nextInt();
                for (Child child : children) {
                    if (child.getId() == enteredId) {
                        return enteredId;
                    }
                }
                System.out.print("Invalid child ID. Please enter a valid child ID: ");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.next();
            }
        }
    }
}
