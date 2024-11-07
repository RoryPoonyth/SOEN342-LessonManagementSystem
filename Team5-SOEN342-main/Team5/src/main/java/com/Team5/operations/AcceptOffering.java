package com.Team5.operations;

import com.Team5.collections.LessonCollection;
import com.Team5.collections.LocationCollection;
import com.Team5.models.Instructor;
import com.Team5.models.Lesson;
import com.Team5.models.Location;
import com.Team5.core.Console;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class AcceptOffering implements Operation {
    private final Instructor instructor;
    private final Scanner scanner;

    public AcceptOffering(Instructor instructor, Scanner scanner) {
        this.instructor = instructor;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        List<Location> locations = LocationCollection.getLocations();
        Console.printTable(locations, Arrays.asList("Lessons"));

        System.out.print("Select a location by entering its ID: ");
        int locationId = requestLocationId(locations);

        List<Lesson> availableLessons = LessonCollection.getUnassignedLessons(locationId);
        Console.printTable(availableLessons, Arrays.asList("Location Id", "Is Available", "Booking", "Assigned Instructor Id", "Id"));

        if (availableLessons.isEmpty()) {
            System.out.println("No available lessons found for the selected location.");
            return;
        }

        Lesson selectedLesson = requestLessonId(availableLessons);
        if (selectedLesson == null) {
            System.out.println("Invalid lesson selection. Assignment process aborted.");
            return;
        }

        if (selectedLesson.getAssignedInstructorId() == -1) {
            selectedLesson.setAssignedInstructorId(instructor.getId());
            selectedLesson.setAvailable(true);
            System.out.println("Lesson successfully assigned to you!");
        } else {
            System.out.println("Lesson is already assigned to another instructor.");
        }
    }

    private int requestLocationId(List<Location> locations) {
        while (true) {
            System.out.print("Please enter a valid location ID (integer): ");
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

    private Lesson requestLessonId(List<Lesson> lessons) {
        while (true) {
            System.out.print("Select a lesson by ID: ");
            try {
                int lessonId = scanner.nextInt();
                for (Lesson lesson : lessons) {
                    if (lesson.getId() == lessonId) {
                        return lesson;
                    }
                }
                System.out.println("Invalid lesson ID. Please enter a valid lesson ID.");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.next();
            }
        }
    }
}
