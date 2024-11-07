package com.Team5.operations;

import com.Team5.collections.LessonCollection;
import com.Team5.collections.LocationCollection;
import com.Team5.enums.LessonType;
import com.Team5.models.Administrator;
import com.Team5.models.Location;
import com.Team5.core.Console;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CreateLesson implements Operation {
    @SuppressWarnings("unused")
	private final Administrator admin;
    private final Scanner scanner;

    public CreateLesson(Administrator admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        List<Location> locations = LocationCollection.getLocations();
        Console.printTable(locations, Arrays.asList("Lessons"));
        System.out.println("\nEnter a location ID to view its corresponding lessons: ");

        int locationId = requestLocationId(locations);
        LessonType lessonType = requestLessonTypeInput();
        String startTime = requestTimeInput("start time (HH:mm)");
        String endTime = requestTimeInput("end time (HH:mm)");

        while (!isEndTimeGreaterThanStartTime(startTime, endTime)) {
            System.out.println("End time must be greater than start time. Please enter valid times again.");
            startTime = requestTimeInput("start time (HH:mm)");
            endTime = requestTimeInput("end time (HH:mm)");
        }

        String title = requestTitle();
        String schedule = requestScheduleInput();
        String confirmation = requestConfirmation(locationId, startTime, endTime, schedule);

        if ("yes".equals(confirmation) && LessonCollection.validateLesson(locationId, startTime, endTime, schedule)
                && LessonCollection.createLesson(locationId, title, lessonType, startTime, endTime, schedule)) {
            System.out.println("Successfully created a new lesson.");
        } else {
            System.out.println("There was an error creating the lesson");
        }
    }

    private String requestConfirmation(Integer locationId, String startTime, String endTime, String schedule) {
        System.out.println("\nPlease confirm the details:");
        System.out.println("Location ID: " + locationId);
        System.out.println("Start Time: " + startTime);
        System.out.println("End Time: " + endTime);
        System.out.println("Schedule: " + schedule);
        System.out.println("Is this correct? (yes/no)");

        return scanner.nextLine().trim().toLowerCase();
    }

    private Integer requestLocationId(List<Location> locations) {
        int locationId;
        while (true) {
            try {
                String input = scanner.nextLine();
                locationId = Integer.parseInt(input);

                if (locationId - 1 >= -1 && locationId - 1 < locations.size()) {
                    return locationId;
                } else {
                    System.out.println("Invalid ID. Please enter a valid location ID: ");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric ID: ");
            }
        }
    }

    private LessonType requestLessonTypeInput() {
        while (true) {
            System.out.println("Please enter the lesson type (PRIVATE or GROUP): ");
            String input = scanner.nextLine().trim().toUpperCase();

            try {
                return LessonType.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid lesson type. Please enter either 'PRIVATE' or 'GROUP'.");
            }
        }
    }

    private String requestTimeInput(String timeType) {
        while (true) {
            System.out.println("Please enter the " + timeType + ": ");
            String time = scanner.nextLine().trim();
            if (time.matches("^(2[0-3]|[01]?[0-9]):[0-5][0-9]$")) {
                return time;
            } else {
                System.out.println("Invalid time format. Please use military time (HH:mm).");
            }
        }
    }

    private boolean isEndTimeGreaterThanStartTime(String startTime, String endTime) {
        String[] startParts = startTime.split(":");
        String[] endParts = endTime.split(":");

        int startHours = Integer.parseInt(startParts[0]);
        int startMinutes = Integer.parseInt(startParts[1]);
        int endHours = Integer.parseInt(endParts[0]);
        int endMinutes = Integer.parseInt(endParts[1]);

        return (endHours > startHours) || (endHours == startHours && endMinutes > startMinutes);
    }

    private String requestScheduleInput() {
        String[] validDays = { "M", "Tu", "W", "Th", "F", "Sa", "Su" };
        while (true) {
            System.out.println("Please enter the schedule (M, Tu, W, Th, F, Sa, Su): ");
            String schedule = scanner.nextLine().trim();
            String[] days = schedule.split("-");

            boolean valid = Arrays.stream(days).allMatch(day -> Arrays.asList(validDays).contains(day));

            if (valid) {
                return schedule;
            } else {
                System.out.println("Invalid schedule format. Please use the format M-Tu-W-Th-F-Sa-Su.");
            }
        }
    }

    private String requestTitle() {
        while (true) {
            System.out.println("Please enter the lesson title: ");
            String title = scanner.nextLine().trim();

            if (!title.isEmpty() && title.length() <= 100) {
                return title;
            } else if (title.isEmpty()) {
                System.out.println("Title cannot be empty. Please enter a valid title.");
            } else {
                System.out.println("Title is too long. Please keep it under 100 characters.");
            }
        }
    }
}
