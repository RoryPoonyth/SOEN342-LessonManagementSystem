package com.Team5.operations;

import com.Team5.collections.BookingCollection;
import com.Team5.collections.ChildCollection;
import com.Team5.collections.InstructorCollection;
import com.Team5.collections.LessonCollection;
import com.Team5.collections.LocationCollection;
import com.Team5.models.Administrator;
import com.Team5.models.Booking;
import com.Team5.models.Child;
import com.Team5.models.Instructor;
import com.Team5.models.Lesson;
import com.Team5.models.Location;
import com.Team5.core.Console;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class AdminViewCancelBookings implements Operation {
    @SuppressWarnings("unused")
	private final Administrator admin;
    private final Scanner scanner;

    public AdminViewCancelBookings(Administrator admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        while (true) {
            List<Booking> allBookings = BookingCollection.getBookings();

            if (allBookings.isEmpty()) {
                System.out.println("No bookings available.");
                return;
            }

            Console.printTable(allBookings, Arrays.asList("Id", "Client Id", "Lesson Id", "Child Id"));

            System.out.print("Enter the ID of a booking to view details or type -1 to exit: ");
            int bookingId = requestBookingId(allBookings);
            if (bookingId == -1) {
                break;
            }

            Booking selectedBooking = BookingCollection.getById(bookingId);

            if (selectedBooking == null) {
                System.out.println("The selected booking does not exist. Please try again.");
                continue;
            }

            Lesson selectedLesson = LessonCollection.getById(selectedBooking.getLessonId());
            Location location = LocationCollection.getById(selectedLesson.getLocationId());
            Instructor instructor = InstructorCollection.getById(selectedLesson.getAssignedInstructorId());
            Child child = ChildCollection.getById(selectedBooking.getChildId());

            displayLessonDetails(selectedLesson, location, instructor, child);

            System.out.print("Type 'd' to delete this booking or 'b' to go back: ");
            String userAction = scanner.next().trim().toLowerCase();

            if (userAction.equals("d")) {
                if (BookingCollection.delete(bookingId)) {
                    System.out.println("Successfully deleted the booking.");
                } else {
                    System.out.println("Failed to delete the booking. Please try again.");
                }
            } else if (!userAction.equals("b")) {
                System.out.println("Invalid selection. Please try again.");
            }
        }
    }

    private void displayLessonDetails(Lesson lesson, Location location, Instructor instructor, Child child) {
        System.out.println("\nLesson Details:");
        System.out.println("Lesson ID: " + lesson.getId());
        System.out.println("Location: " + (location != null ? location.getName() : "Unknown"));
        System.out.println("Instructor: " + (instructor != null ? instructor.getFirstName() + " " + instructor.getLastName() : "Unknown"));
        if (child != null) {
            System.out.println("Child: " + child.getFirstName() + " " + child.getLastName());
        }
        System.out.println();
    }

    private int requestBookingId(List<Booking> allBookings) {
        while (true) {
            try {
                int bookingId = scanner.nextInt();
                if (bookingId == -1) {
                    return -1;
                }
                for (Booking booking : allBookings) {
                    if (booking.getId() == bookingId) {
                        return bookingId;
                    }
                }
                System.out.println("Invalid booking ID. Please try again.");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next();
            }
        }
    }
}