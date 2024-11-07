package com.Team5.operations;

import com.Team5.collections.BookingCollection;
import com.Team5.collections.ClientCollection;
import com.Team5.collections.InstructorCollection;
import com.Team5.collections.LessonCollection;
import com.Team5.collections.LocationCollection;
import com.Team5.models.Booking;
import com.Team5.models.Client;
import com.Team5.models.Instructor;
import com.Team5.models.Lesson;
import com.Team5.models.Location;
import com.Team5.core.Console;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ViewEditOfferings implements Operation {
    private final Instructor instructor;
    private final Scanner scanner;

    public ViewEditOfferings(Instructor instructor, Scanner scanner) {
        this.instructor = instructor;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        while (true) {
            List<Lesson> instructorLessons = LessonCollection.getByInstructorId(instructor.getId());

            if (instructorLessons.isEmpty()) {
                System.out.println("You have no assigned lessons.");
                return;
            }

            Console.printTable(instructorLessons, Arrays.asList("Id", "Location Id", "Is Available", "Assigned Instructor Id", "Booking Id"));

            System.out.print("Enter the ID of a lesson to view details or type -1 to exit: ");
            Lesson selectedLesson = requestLessonId(instructorLessons);

            if (selectedLesson == null) {
                // User typed -1 to exit
                System.out.println("Exiting to the main menu.");
                break; // Exit the loop
            }

            Location location = LocationCollection.getById(selectedLesson.getLocationId());
            Booking booking = BookingCollection.getByLessonId(selectedLesson.getId());
            Client client = booking != null ? ClientCollection.getById(booking.getClientId()) : null;

            displayLessonDetails(selectedLesson, location, client);

            System.out.print("Type 'u' to un-assign yourself from this offering or 'b' to go back: ");
            String userAction = scanner.next().trim().toLowerCase();

            if (userAction.equals("u")) {
                selectedLesson.setAssignedInstructorId(-1);
                LessonCollection.updateLesson(selectedLesson);

                InstructorCollection.update(instructor);

                System.out.println("You have successfully un-assigned yourself from the lesson.");
            } else if (!userAction.equals("b")) {
                System.out.println("Invalid selection. Please try again.");
            }
        }
    }

    private void displayLessonDetails(Lesson lesson, Location location, Client client) {
        System.out.println("\nLesson Details:");
        System.out.println("Lesson ID: " + lesson.getId());
        System.out.println("Location: " + (location != null ? location.getName() : "Unknown"));
        System.out.println("Client: " + (client != null ? client.getFirstName() + " " + client.getLastName() : "No client assigned"));
        System.out.println();
    }

    private Lesson requestLessonId(List<Lesson> lessons) {
        while (true) {
            try {
                int lessonId = scanner.nextInt();
                if (lessonId == -1) {
                    return null;
                }
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

