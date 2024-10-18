import java.util.*;

public class Main {
    public static void main(String[] args) {
        LessonBookingSystem system = new LessonBookingSystem();

        // Adding locations
        system.addLocation("EV-Building Gym Room 7", "Montreal", "Gym");

        // Registering instructors
        Set<String> specializations = new HashSet<>(Arrays.asList("Judo", "Swimming"));
        Set<String> cities = new HashSet<>(Arrays.asList("Montreal", "Laval"));
        system.registerInstructor("Grace", "514-XXX-XXXX", specializations, cities);

        // Adding time slots
        system.addTimeSlotToLocation("EV-Building Gym Room 7", "Sunday", "12:00", "13:00", "Group");
        system.addTimeSlotToLocation("EV-Building Gym Room 7", "Sunday", "13:00", "14:00", "Group");
        system.addTimeSlotToLocation("EV-Building Gym Room 7", "Sunday", "14:00", "14:30", "Private");

        // Assigning instructor
        system.assignInstructorToSlot("EV-Building Gym Room 7", "Sunday", "12:00", "Grace");

        // Viewing available slots
        system.viewAvailableSlots();
    }
}