package com.Team5.operations;

import com.Team5.collections.LessonCollection;
import com.Team5.collections.LocationCollection;
import com.Team5.models.Lesson;
import com.Team5.models.Location;
import com.Team5.core.Console;

import java.util.Arrays;
import java.util.List;

public class ViewOfferings implements Operation {
    @Override
    public void execute() {
        List<Lesson> allLessons = LessonCollection.getLessons();

        if (allLessons.isEmpty()) {
            System.out.println("No lessons are currently available.");
            return;
        }

        Console.printTable(allLessons, Arrays.asList("Id", "Title", "Type", "Location Id", "Is Available", "Assigned Instructor Id", "Start Time", "End Time"));

        for (Lesson lesson : allLessons) {
            Location location = LocationCollection.getById(lesson.getLocationId());
            displayLessonDetails(lesson, location);
        }
    }

    private void displayLessonDetails(Lesson lesson, Location location) {
        System.out.println("\nLesson Details:");
        System.out.println("Lesson ID: " + lesson.getId());
        System.out.println("Title: " + lesson.getTitle());
        System.out.println("Type: " + lesson.getType());
        System.out.println("Location: " + (location != null ? location.getName() : "Unknown"));
        System.out.println("Is Available: " + (lesson.getIsAvailable() ? "Available" : "Non-available"));
        System.out.println("Start Time: " + lesson.getStartTime());
        System.out.println("End Time: " + lesson.getEndTime());
        System.out.println("Days: " + lesson.getSchedule());
        System.out.println();
    }
}
