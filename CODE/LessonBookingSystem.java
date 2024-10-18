import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class LessonBookingSystem {
    List<Location> locations;
    List<Instructor> instructors;
    List<Client> clients;

    public LessonBookingSystem() {
        locations = new ArrayList<>();
        instructors = new ArrayList<>();
        clients = new ArrayList<>();
        createDatabase();
    }

    private void createDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:lesson_booking.db")) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS locations (name TEXT, city TEXT, type TEXT)");
                stmt.execute("CREATE TABLE IF NOT EXISTS instructors (name TEXT, phone_number TEXT)");
                stmt.execute("CREATE TABLE IF NOT EXISTS clients (name TEXT, age INTEGER, guardian_name TEXT)");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addLocation(String name, String city, String type) {
        Location location = new Location(name, city, type);
        location.saveToDatabase();
        locations.add(location);
    }

    public void registerInstructor(String name, String phoneNumber, Set<String> specializations, Set<String> availableCities) {
        Instructor instructor = new Instructor(name, phoneNumber, specializations, availableCities);
        instructor.saveToDatabase();
        instructors.add(instructor);
    }

    public void registerClient(String name, int age, String guardianName) {
        Client client = new Client(name, age, guardianName);
        client.saveToDatabase();
        clients.add(client);
    }

    public void addTimeSlotToLocation(String locationName, String day, String startTime, String endTime, String type) {
        for (Location location : locations) {
            if (location.name.equals(locationName)) {
                location.availableSlots.add(new TimeSlot(day, startTime, endTime, type));
            }
        }
    }

    public void assignInstructorToSlot(String locationName, String day, String startTime, String instructorName) {
        for (Location location : locations) {
            if (location.name.equals(locationName)) {
                for (TimeSlot slot : location.availableSlots) {
                    if (slot.day.equals(day) && slot.startTime.equals(startTime) && slot.isAvailable) {
                        for (Instructor instructor : instructors) {
                            if (instructor.name.equals(instructorName)) {
                                slot.instructor = instructor;
                                slot.isAvailable = false;
                            }
                        }
                    }
                }
            }
        }
    }

    public void viewAvailableSlots() {
        for (Location location : locations) {
            System.out.println("Location: " + location.name + ", City: " + location.city);
            for (TimeSlot slot : location.availableSlots) {
                if (slot.isAvailable) {
                    System.out.println("Day: " + slot.day + ", Time: " + slot.startTime + " - " + slot.endTime + ", Type: " + slot.type);
                }
            }
        }
    }
}