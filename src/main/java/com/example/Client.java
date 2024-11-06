package src.main.java.com.example;
import javax.persistence.*;

import org.hibernate.Session;

import java.util.Scanner;
import java.util.List;

@Entity
@Table(name = "clients")
@DiscriminatorValue("client")
public class Client extends User {

	private static final long serialVersionUID = 4468426228590476231L;

	@Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "is_legal_guardian", nullable = false)
    private boolean isLegalGuardian;

    @OneToOne(mappedBy = "guardian", cascade = CascadeType.ALL)
    private Minor minor;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookings> bookings;

    public Client(String name, String password, String phoneNumber, boolean isLegalGuardian) {
        super(name, password, "client");
        this.phoneNumber = phoneNumber;
        this.isLegalGuardian = isLegalGuardian;
    }

    public Client() {
    }

    @Override
    public String toString() {
        if (isLegalGuardian) {
            return String.format("Client %d, %s, (%s) is a legal guardian of %s", getId(), getName(), phoneNumber, minor);
        }
        return String.format("Client %d (%s) is a client", getId(), phoneNumber);
    }

    public int getId() {
        return super.getId();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isLegalGuardian() {
        return isLegalGuardian;
    }

    public Minor getMinor() {
        return minor;
    }

    public List<Bookings> getBookings() {
        return bookings;
    }

    public void clientMenu(Session entityManager) {
        try (Scanner scanner = new Scanner(System.in)) {
			OfferingCatalog offeringsCatalog = OfferingCatalog.getInstance(entityManager);
			BookingsCatalog bookingsCatalog = BookingsCatalog.getInstance(entityManager);

			String clientMenuOptions = """
			        Client Options:
			        1. View Available Offerings 
			        2. Book an Available Offering
			        3. View My Bookings
			        4. Cancel a Booking
			        5. View Minor's Bookings (Only for Legal Guardians)
			        6. Logout and return to main menu
			        """;

			while (true) {
			    System.out.println(clientMenuOptions);
			    System.out.print("\nSelect an option: ");
			    int choice = scanner.nextInt();
			    scanner.nextLine();

			    switch (choice) {
			        case 1:
			            System.out.println("\n--------View Offerings--------");
			            List<Offering> offerings = offeringsCatalog.getOfferingsWithInstructor();
			            if (offerings.isEmpty()) {
			                System.out.println("No available offerings with an instructor at this time.");
			            } else {
			                System.out.println("\nAvailable Offerings:");
			                offerings.forEach(offering -> System.out.println(offering.reprClient()));
			            }
			            break;
			        case 2:
			            System.out.println("\n--------Book an Available Offering--------");
			            System.out.println("\nSpecializations Offered:");
			            SpecializationType[] specializations = SpecializationType.values();
			            for (int i = 0; i < specializations.length; i++) {
			                System.out.printf("%d. %s\n", i + 1, specializations[i].getValue());
			            }
			            System.out.print("\nSelect the number corresponding to the specialization you'd like to search for: ");
			            int specializationChoice = scanner.nextInt();
			            scanner.nextLine();
			            if (specializationChoice < 1 || specializationChoice > specializations.length) {
			                System.out.println("Invalid selection. Please enter a valid number corresponding to a specialization.");
			                break;
			            }
			            SpecializationType chosenSpecialization = specializations[specializationChoice - 1];
			            List<Offering> availableOfferings = offeringsCatalog.getOfferingsWithInstructorBySpecialization(chosenSpecialization);
			            if (availableOfferings.isEmpty()) {
			                System.out.printf("No available offerings for %s at this time.\n", chosenSpecialization.getValue());
			            } else {
			                System.out.printf("\nAvailable Offerings for %s:\n", chosenSpecialization.getValue());
			                availableOfferings.forEach(offering -> System.out.println(offering.reprClient()));
			                System.out.print("\nEnter the ID of the offering you'd like to book: ");
			                int offeringId = scanner.nextInt();
			                scanner.nextLine();
			                Offering selectedOffering = offeringsCatalog.getOfferingById(offeringId);
			                if (selectedOffering != null) {
			                    System.out.print("Is this booking for a minor? (yes/no): ");
			                    String isBookingForMinor = scanner.nextLine().toLowerCase().trim();
			                    if (isBookingForMinor.equals("yes") && isLegalGuardian && minor != null) {
			                        bookingsCatalog.createBooking(this, selectedOffering, minor.getId());
			                    } else {
			                        bookingsCatalog.createBooking(this, selectedOffering, null);
			                    }
			                } else {
			                    System.out.println("Invalid offering ID.");
			                }
			            }
			            break;
			        case 3:
			            System.out.println("\n--------View My Bookings--------");
			            List<Bookings> clientBookings = bookingsCatalog.getClientBookings(this);
			            if (clientBookings.isEmpty()) {
			                System.out.println("You have no bookings.");
			            } else {
			                System.out.println("\nYour Bookings:");
			                clientBookings.forEach(booking -> System.out.println(booking.reprClient()));
			            }
			            break;
			        case 4:
			            System.out.println("\n--------Cancel a Booking--------");
			            clientBookings = bookingsCatalog.getClientBookings(this);
			            if (clientBookings.isEmpty()) {
			                System.out.println("You have no bookings to cancel.");
			                break;
			            }
			            System.out.println("\nYour Current Bookings:");
			            clientBookings.forEach(booking -> System.out.println(booking.reprClient()));
			            System.out.print("\nEnter the ID of the booking you'd like to cancel: ");
			            int bookingId = scanner.nextInt();
			            scanner.nextLine();
			            Bookings selectedBooking = bookingsCatalog.getBooking(bookingId);
			            if (selectedBooking != null) {
			                bookingsCatalog.cancelBooking(this, selectedBooking);
			            } else {
			                System.out.println("Invalid booking ID.");
			            }
			            break;

			        case 5:
			            if (!isLegalGuardian) {
			                System.out.println("You are not a legal guardian. This option is not available.");
			                break;
			            }
			            if (minor == null) {
			                System.out.println("No minor associated with this account.");
			                break;
			            }
			            System.out.printf("\nBookings for %s:\n", minor.getName());
			            List<Bookings> minorBookings = bookingsCatalog.getMinorBookings(minor.getId());
			            if (minorBookings.isEmpty()) {
			                System.out.printf("No bookings found for minor: %s.\n", minor.getName());
			            } else {
			                minorBookings.forEach(booking -> System.out.println(booking.reprClient()));
			            }
			            break;
			        case 6:
			            System.out.println("\nLogging out...");
			            return;
			        default:
			            System.out.println("\nInvalid choice. Please enter a valid number between 1 and 6.");
			            break;
			    }
			}
		}
    }
}
