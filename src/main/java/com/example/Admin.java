package src.main.java.com.example;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.persistence.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Entity
@DiscriminatorValue("admin")
public class Admin extends User {

    private static final long serialVersionUID = 2541454063286190773L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    public Admin(String name, String password) {
        super(name, password, "admin");
    }

    public Admin() {
    }

    @Override
    public String getType() {
        return "admin";
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String toString() {
        return String.format("Admin{id=%d, name='%s'}", id, getName());
    }

    public void adminMenu(Session entityManager) {
        try (Scanner scanner = new Scanner(System.in)) {
            OfferingCatalog offeringCatalog = OfferingCatalog.getInstance(entityManager);

            while (true) {
                System.out.println("\nAdmin Options:\n" +
                        "1. View Offerings\n" +
                        "2. Create Offering\n" +
                        "3. Cancel Offering\n" +
                        "4. Delete Client/Instructor Account\n" +
                        "5. View Client Bookings (Optionally Cancel Client Booking)\n" +
                        "6. Add Location\n" +
                        "7. View Location Schedule\n" +
                        "8. Delete Location\n" +
                        "9. Get All Locations\n" +
                        "10. Get Location from ID or City (and optionally Name and Address)\n" +
                        "11. Logout and return to main menu");

                System.out.print("\nSelect an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                switch (choice) {
                    case 1:
                        viewOfferings(offeringCatalog);
                        break;
                    case 2:
                        createOffering(offeringCatalog, entityManager);
                        break;
                    case 3:
                        cancelOffering(offeringCatalog);
                        break;
                    case 4:
                        deleteAccount(entityManager);
                        break;
                    case 5:
                        viewClientBookings(entityManager);
                        break;
                    case 6:
                        addLocation(entityManager);
                        break;
                    case 7:
                        viewLocationSchedule(entityManager);
                        break;
                    case 8:
                        deleteLocation(entityManager);
                        break;
                    case 9:
                        getAllLocations(entityManager);
                        break;
                    case 10:
                        getLocationDetails(entityManager);
                        break;
                    case 11:
                        System.out.println("\nLogging out and returning to main menu. Goodbye!");
                        return;
                    default:
                        System.out.println("\nInvalid choice. Please enter a number between 1 and 11.");
                }
            }
        }
    }

    private void viewOfferings(OfferingCatalog offeringCatalog) {
        System.out.println("Viewing all offerings...");
        List<Offering> offerings = offeringCatalog.getAllOfferings(null, null, null, true);
        offerings.forEach(System.out::println);
    }

    private void createOffering(OfferingCatalog offeringCatalog, Session entityManager) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter offering type (e.g., group, private): ");
            String typeString = scanner.nextLine();
            OfferingType type = OfferingType.valueOf(typeString.toUpperCase());
            System.out.print("Enter offering specialization: ");
            String specializationString = scanner.nextLine();
            SpecializationType specialization = SpecializationType.valueOf(specializationString.toUpperCase());
            System.out.print("Enter location ID: ");
            int locationId = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter capacity: ");
            int capacity = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter start date (YYYY-MM-DD): ");
            String startDate = scanner.nextLine();
            System.out.print("Enter end date (YYYY-MM-DD): ");
            String endDate = scanner.nextLine();

            Location location = entityManager.get(Location.class, locationId);
            Timeslot timeslot = new Timeslot("Monday", LocalTime.of(9, 0), LocalDate.parse(startDate), LocalTime.of(17, 0), LocalDate.parse(endDate), null);

            try {
                Offering newOffering = offeringCatalog.createOffering(type, specialization, location, capacity, timeslot);
                System.out.println("Offering created successfully: " + newOffering);
            } catch (IllegalArgumentException e) {
                System.out.println("Failed to create offering: " + e.getMessage());
            }
        }
    }

    private void cancelOffering(OfferingCatalog offeringCatalog) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter offering ID to cancel: ");
            int offeringId = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            try {
                Offering offering = offeringCatalog.getOfferingById(offeringId);
                offeringCatalog.cancelOffering(offering);
                System.out.println("Offering with ID " + offeringId + " has been cancelled.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void deleteAccount(Session entityManager) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter account ID to delete: ");
            int accountId = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            User user = entityManager.get(User.class, accountId);
            if (user != null) {
                Transaction transaction = entityManager.beginTransaction();
                try {
                    entityManager.delete(user);
                    transaction.commit();
                    System.out.println("Account with ID " + accountId + " has been deleted.");
                } catch (Exception e) {
                    if (transaction != null) transaction.rollback();
                    System.out.println("Failed to delete account: " + e.getMessage());
                }
            } else {
                System.out.println("Account with ID " + accountId + " not found.");
            }
        }
    }

    private void viewClientBookings(Session entityManager) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter client ID to view bookings: ");
            int clientId = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            List<Bookings> bookings = entityManager.createQuery("from Bookings where client.id = :clientId", Bookings.class)
                    .setParameter("clientId", clientId)
                    .list();
            if (bookings.isEmpty()) {
                System.out.println("No bookings found for client with ID " + clientId + ".");
            } else {
                bookings.forEach(System.out::println);
                System.out.print("Do you want to cancel a booking? (yes/no): ");
                String response = scanner.nextLine();
                if (response.equalsIgnoreCase("yes")) {
                    System.out.print("Enter booking ID to cancel: ");
                    int bookingId = scanner.nextInt();
                    scanner.nextLine();  // Consume newline

                    Bookings booking = entityManager.get(Bookings.class, bookingId);
                    if (booking != null) {
                        Transaction transaction = entityManager.beginTransaction();
                        try {
                            entityManager.delete(booking);
                            transaction.commit();
                            System.out.println("Booking with ID " + bookingId + " has been cancelled.");
                        } catch (Exception e) {
                            if (transaction != null) transaction.rollback();
                            System.out.println("Failed to cancel booking: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Booking with ID " + bookingId + " not found.");
                    }
                }
            }
        }
    }

    private void addLocation(Session entityManager) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter location name: ");
            String name = scanner.nextLine();
            System.out.print("Enter location address: ");
            String address = scanner.nextLine();
            System.out.print("Enter capacity: ");
            int capacity = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter city: ");
            String city = scanner.nextLine();
            System.out.print("Enter the number of space types: ");
            int spaceTypeCount = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            List<String> spaceTypes = new ArrayList<>();
            for (int i = 0; i < spaceTypeCount; i++) {
                System.out.print("Enter space type " + (i + 1) + ": ");
                spaceTypes.add(scanner.nextLine());
            }

            Location newLocation = new Location(name, address, capacity, new City(city), spaceTypes);
            Transaction transaction = entityManager.beginTransaction();
            try {
                entityManager.save(newLocation);
                transaction.commit();
                System.out.println("Location added successfully: " + newLocation);
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                System.out.println("Failed to add location: " + e.getMessage());
            }
        }
    }

    private void viewLocationSchedule(Session entityManager) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter location ID to view schedule: ");
            int locationId = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            List<Timeslot> schedule = entityManager.createQuery("from Timeslot where location.id = :locationId", Timeslot.class)
                    .setParameter("locationId", locationId)
                    .list();
            if (schedule.isEmpty()) {
                System.out.println("No schedule found for location with ID " + locationId + ".");
            } else {
                schedule.forEach(System.out::println);
            }
        }
    }

    private void deleteLocation(Session entityManager) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter location ID to delete: ");
            int locationId = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            Location location = entityManager.get(Location.class, locationId);
            if (location != null) {
                Transaction transaction = entityManager.beginTransaction();
                try {
                    entityManager.delete(location);
                    transaction.commit();
                    System.out.println("Location with ID " + locationId + " has been deleted.");
                } catch (Exception e) {
                    if (transaction != null) transaction.rollback();
                    System.out.println("Failed to delete location: " + e.getMessage());
                }
            } else {
                System.out.println("Location with ID " + locationId + " not found.");
            }
        }
    }

    private void getAllLocations(Session entityManager) {
        System.out.println("Getting all locations...");
        List<Location> locations = entityManager.createQuery("from Location", Location.class).list();
        locations.forEach(System.out::println);
    }

    private void getLocationDetails(Session entityManager) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter location ID or city: ");
            String input = scanner.nextLine();

            try {
                int locationId = Integer.parseInt(input);
                Location location = entityManager.get(Location.class, locationId);
                if (location != null) {
                    System.out.println(location);
                } else {
                    System.out.println("Location with ID " + locationId + " not found.");
                }
            } catch (NumberFormatException e) {
                List<Location> locations = entityManager.createQuery("from Location where city = :city", Location.class)
                        .setParameter("city", input)
                        .list();
                if (locations.isEmpty()) {
                    System.out.println("No locations found in city " + input + ".");
                } else {
                    locations.forEach(System.out::println);
                }
            }
        }
    }
}
