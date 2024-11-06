package src.main.java.com.example;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class LessonManagementSystem {
    private static Session session;

    public static void createSampleObjects(Session session) {
        UsersCatalog userCatalog = UsersCatalog.getInstance(session);

        try {
            User newAdmin = userCatalog.registerAdmin("admin", "pass");

            User instructor1 = userCatalog.registerInstructor( 
                    "instructor1",
                    "pass",
                    "1234567890",
                    Arrays.asList(SpecializationType.HOCKEY, SpecializationType.SOCCER),
                    Arrays.asList("Montreal", "Laval")
            );

            User instructor2 = userCatalog.registerInstructor(
                    "instructor2",
                    "pass",
                    "1234567891",
                    Arrays.asList(SpecializationType.SWIM, SpecializationType.YOGA),
                    Arrays.asList("Terrebonne", "Laval")
            );

            User instructor3 = userCatalog.registerInstructor(
                    "instructor3",
                    "pass",
                    "1234567892",
                    Arrays.asList(SpecializationType.DANCE, SpecializationType.SOCCER),
                    Arrays.asList("Montreal", "Dorval")
            );
        } catch (Exception e) {
            System.out.println("Error creating admin: " + e.getMessage());
        }
    }

    public static void createLocationsAndOfferings(Session session) {
        OfferingCatalog offeringsCatalog = OfferingCatalog.getInstance(session);
        LocationsCatalog locationCatalog = LocationsCatalog.getInstance(session);

        try {
        	Location location1 = locationCatalog.createLocation(
        	        "TD Bank",
        	        "1234 Street",
        	        50,
        	        new City("Montreal"), // Assuming a constructor like `new City("Montreal")`
        	        Arrays.asList("RINK", "FIELD")
        	);

        	Location location2 = locationCatalog.createLocation(
        	        "FB Dungeon",
        	        "5678 Street",
        	        20,
        	        new City("Laval"),
        	        Arrays.asList("FIELD", "POOL")
        	);

        	Location location3 = locationCatalog.createLocation(
        	        "Googleplex",
        	        "91011 Street",
        	        100,
        	        new City("Terrebonne"),
        	        Arrays.asList("POOL", "GYM")
        	);

        	Location location4 = locationCatalog.createLocation(
        	        "Amazon",
        	        "121314 Street",
        	        50,
        	        new City("Dorval"),
        	        Arrays.asList("STUDIO", "GYM")
        	);


            Timeslot timeslot1 = new Timeslot("Monday", LocalTime.of(9, 0), LocalDate.of(2024, 10, 16), LocalTime.of(10, 0), LocalDate.of(2024, 10, 16), location1.getSchedule());
            Timeslot timeslot2 = new Timeslot("Tuesday", LocalTime.of(10, 0), LocalDate.of(2024, 10, 17), LocalTime.of(11, 0), LocalDate.of(2024, 10, 17), location2.getSchedule());
            Timeslot timeslot3 = new Timeslot("Wednesday", LocalTime.of(11, 0), LocalDate.of(2024, 10, 18), LocalTime.of(12, 0), LocalDate.of(2024, 10, 18), location3.getSchedule());
            Timeslot timeslot4 = new Timeslot("Thursday", LocalTime.of(12, 0), LocalDate.of(2024, 10, 19), LocalTime.of(13, 0), LocalDate.of(2024, 10, 19), location4.getSchedule());


            Offering offering1 = offeringsCatalog.createOffering(OfferingType.PRIVATE, SpecializationType.HOCKEY, location1, 50, timeslot1);
            Offering offering2 = offeringsCatalog.createOffering(OfferingType.GROUP, SpecializationType.SOCCER, location2, 20, timeslot2);
            Offering offering3 = offeringsCatalog.createOffering(OfferingType.PRIVATE, SpecializationType.SWIM, location3, 100, timeslot3);
            Offering offering4 = offeringsCatalog.createOffering(OfferingType.GROUP, SpecializationType.YOGA, location4, 50, timeslot4);

        } catch (Exception e) {
            System.out.println("Error creating location: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
    	session = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {
            System.out.println("\n\nWelcome to the Lesson Management System");
            createSampleObjects(session);
            createLocationsAndOfferings(session);

            try (Scanner scanner = new Scanner(System.in)) {
				boolean running = true;

				while (running) {
				    System.out.println("\nOptions:\n1. Login\n2. Register as Client\n3. Register as Instructor\n4. Register as Admin\n5. View Offerings (Public)\n6. Exit");
				    System.out.print("Select an option: ");

				    int choice = scanner.nextInt();
				    scanner.nextLine();

				    switch (choice) {
				        case 1:
				            System.out.println("\n--------Login--------");
				            System.out.print("Enter user type (client/instructor/admin): ");
				            String userType = scanner.nextLine().toLowerCase();
				            System.out.print("Enter username: ");
				            String username = scanner.nextLine();
				            System.out.print("Enter password: ");
				            String password = scanner.nextLine();
				            User user = null;
				            try {
				                user = UsersCatalog.getInstance(session).login(userType, username, password);
				                if (user != null) {
				                    System.out.println("Login successful. Welcome, " + user.getName() + "!");
				                } else {
				                    System.out.println("Invalid credentials.");
				                }
				            } catch (Exception e) {
				                System.out.println("Login error: " + e.getMessage());
				            }
				            break;
				        case 2:
				            System.out.println("\n--------Register as Client--------");
				            System.out.print("Enter name: ");
				            String clientName = scanner.nextLine();
				            System.out.print("Enter phone number: ");
				            String clientPhone = scanner.nextLine();
				            System.out.print("Enter password: ");
				            String clientPassword = scanner.nextLine();
				            try {
				                User client = UsersCatalog.getInstance(session).registerClient(clientName, clientPassword, clientPhone, false);
				                System.out.println("Client registered successfully: " + client.getName());
				            } catch (Exception e) {
				                System.out.println("Registration error: " + e.getMessage());
				            }
				            break;
				        case 3:
				            System.out.println("\n--------Register as Instructor--------");
				            System.out.print("Enter name: ");
				            String instructorName = scanner.nextLine();
				            System.out.print("Enter phone number: ");
				            String instructorPhone = scanner.nextLine();
				            System.out.print("Enter password: ");
				            String instructorPassword = scanner.nextLine();
				            System.out.print("Enter specializations (comma separated): ");
				            String[] specializations = scanner.nextLine().split(",");
				            System.out.print("Enter cities (comma separated): ");
				            String[] cities = scanner.nextLine().split(",");
				            try {
				                User instructor = UsersCatalog.getInstance(session).registerInstructor(
				                        instructorName,
				                        instructorPassword,
				                        instructorPhone,
				                        Arrays.asList(specializations).stream().map(String::trim).map(SpecializationType::valueOf).toList(),
				                        Arrays.asList(cities)
				                );
				                System.out.println("Instructor registered successfully: " + instructor.getName());
				            } catch (Exception e) {
				                System.out.println("Registration error: " + e.getMessage());
				            }
				            break;
				        case 4:
				            System.out.println("\n--------Register as Admin--------");
				            System.out.print("Enter name: ");
				            String adminName = scanner.nextLine();
				            System.out.print("Enter password: ");
				            String adminPassword = scanner.nextLine();
				            try {
				                User admin = UsersCatalog.getInstance(session).registerAdmin(adminName, adminPassword);
				                System.out.println("Admin registered successfully: " + admin.getName());
				            } catch (Exception e) {
				                System.out.println("Registration error: " + e.getMessage());
				            }
				            break;
				        case 5:
				            System.out.println("\n--------View Offerings (Public)--------");
				            OfferingCatalog offeringsCatalog = OfferingCatalog.getInstance(session);
				            List<Offering> offerings = offeringsCatalog.getOfferingsWithInstructor();
				            if (offerings.isEmpty()) {
				                System.out.println("No offerings available at this time.");
				            } else {
				                System.out.println("\nAvailable Offerings:");
				                for (Offering offering : offerings) {
				                    System.out.println(offering.toString());
				                }
				            }
				            break;
				        case 6:
				            System.out.println("\nGoodbye!");
				            running = false;
				            break;
				        default:
				            System.out.println("Invalid choice. Please enter a number between 1 and 6.");
				    }
				}
			}
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
