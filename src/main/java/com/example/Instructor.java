package src.main.java.com.example;
import javax.persistence.*;
import org.hibernate.Session;
import java.util.List;
import java.util.Scanner;

@Entity
@Table(name = "instructors")
@DiscriminatorValue("instructor")
public class Instructor extends User {

	private static final long serialVersionUID = -7999056351282960082L;

	@Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @ElementCollection
    @CollectionTable(name = "instructor_specializations", joinColumns = @JoinColumn(name = "instructor_id"))
    @Column(name = "specialization", nullable = false)
    private List<SpecializationType> specializations;

    @ElementCollection
    @CollectionTable(name = "instructor_available_cities", joinColumns = @JoinColumn(name = "instructor_id"))
    @Column(name = "available_city", nullable = false)
    private List<String> availableCities;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Offering> offerings;

    public Instructor(String name, String password, String phoneNumber, List<SpecializationType> specializations, List<String> availableCities) {
        super(name, password, "instructor");
        this.phoneNumber = phoneNumber;
        this.specializations = specializations;
        this.availableCities = availableCities;
    }

    public Instructor() {
    }

    @Override
    public String toString() {
        return String.format("Instructor %d %s (%s), has the following specializations: %s and the following cities: %s", getId(), getName(), phoneNumber, specializations, availableCities);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<SpecializationType> getSpecializations() {
        return specializations;
    }

    public List<String> getAvailableCities() {
        return availableCities;
    }

    public List<Offering> getOfferings() {
        return offerings;
    }

    public void instructorMenu(Session entityManager) {
        try (Scanner scanner = new Scanner(System.in)) {
			OfferingCatalog offeringsCatalog = OfferingCatalog.getInstance(entityManager);
			UsersCatalog usersCatalog = UsersCatalog.getInstance(entityManager);

			String instructorMenuOptions = """
			        Instructor Options:
			        1. Select Offering
			        2. View my Offerings
			        3. Modify my Offering
			        4. Modify my Account
			        5. Logout and return to main menu
			        """;

			while (true) {
			    System.out.println(instructorMenuOptions);
			    System.out.print("\nSelect an option: ");
			    int choice = scanner.nextInt();
			    scanner.nextLine();

			    switch (choice) {
			        case 1:
			            System.out.println("\n--------Select Offering--------");
			            List<Offering> availableOfferings = offeringsCatalog.getAvailableOfferingsForInstructor(availableCities, specializations);
			            if (availableOfferings.isEmpty()) {
			                System.out.println("No available offerings found that match your criteria.");
			            } else {
			                System.out.println("\nAvailable Offerings:");
			                availableOfferings.forEach(offering -> System.out.println(offering));
			                System.out.print("\nEnter the ID of the offering you want to select (or 'q' to quit): ");
			                String selectedOfferingIdInput = scanner.nextLine();
			                if (selectedOfferingIdInput.equalsIgnoreCase("q")) {
			                    break;
			                }
			                try {
			                    int selectedOfferingId = Integer.parseInt(selectedOfferingIdInput);
			                    Offering selectedOffering = availableOfferings.stream()
			                            .filter(off -> off.getId() == selectedOfferingId)
			                            .findFirst()
			                            .orElse(null);
			                    if (selectedOffering != null) {
			                        if (offeringsCatalog.hasTimeConflict(offerings, selectedOffering)) {
			                            System.out.println("You cannot book this offering because it conflicts with an existing offering.");
			                        } else {
			                            offeringsCatalog.assignInstructorToOffering(this, selectedOffering);
			                            System.out.printf("Successfully selected offering with ID %d and assigned it to you.%n", selectedOffering.getId());
			                        }
			                    } else {
			                        System.out.println("Invalid offering ID. Please select a valid offering from the list.");
			                    }
			                } catch (NumberFormatException e) {
			                    System.out.println("Invalid input. Please enter a valid offering ID.");
			                }
			            }
			            break;
			        case 2:
			            System.out.println("\n--------View my Offerings--------");
			            List<Offering> myOfferings = offeringsCatalog.getOfferingsByInstructorId(getId());
			            if (myOfferings.isEmpty()) {
			                System.out.println("You have no offerings.");
			            } else {
			                System.out.println("\nYour Offerings:");
			                myOfferings.forEach(offering -> System.out.println(offering));
			            }
			            break;
			        case 3:
			            System.out.println("\n--------Modify my Offering (Remove Myself)--------");
			            myOfferings = offeringsCatalog.getOfferingsByInstructorId(getId());
			            if (myOfferings.isEmpty()) {
			                System.out.println("You have no offerings to modify.");
			            } else {
			                System.out.println("\nYour Offerings:");
			                myOfferings.forEach(offering -> System.out.println(offering));
			                System.out.print("\nEnter the ID of the offering you want to remove yourself from (or 'q' to quit): ");
			                String selectedOfferingIdInput = scanner.nextLine();
			                if (selectedOfferingIdInput.equalsIgnoreCase("q")) {
			                    break;
			                }
			                try {
			                    int selectedOfferingId = Integer.parseInt(selectedOfferingIdInput);
			                    Offering selectedOffering = myOfferings.stream()
			                            .filter(off -> off.getId() == selectedOfferingId)
			                            .findFirst()
			                            .orElse(null);
			                    if (selectedOffering != null) {
			                        offeringsCatalog.removeInstructorFromOffering(this, selectedOffering);
			                        System.out.printf("You have successfully removed yourself from offering with ID %d.%n", selectedOffering.getId());
			                    } else {
			                        System.out.println("Invalid offering ID. Please select a valid offering from the list.");
			                    }
			                } catch (NumberFormatException e) {
			                    System.out.println("Invalid input. Please enter a valid offering ID.");
			                }
			            }
			            break;
			        case 4:
			            System.out.println("\n--------Modify my Account--------");
			            System.out.printf("\nCurrent Account Details:\nName: %s\nPhone Number: %s\nSpecializations: %s\nAvailable Cities: %s\n",
			                    getName(), phoneNumber, specializations, availableCities);
			            System.out.println("\nPlease enter the new information for your account. Press 'enter' to keep the current information.");
			            System.out.print("\nEnter new name (or press 'q' to quit): ");
			            String newName = scanner.nextLine().trim();
			            if (!newName.equalsIgnoreCase("q") && !newName.isEmpty()) {
			                setName(newName);
			            }
			            System.out.print("Enter new phone number (or press 'q' to quit): ");
			            String newPhone = scanner.nextLine().trim();
			            if (!newPhone.equalsIgnoreCase("q") && !newPhone.isEmpty()) {
			                phoneNumber = newPhone;
			            }
			            System.out.print("Enter new password (or press 'q' to quit): ");
			            String newPassword = scanner.nextLine().trim();
			            if (!newPassword.equalsIgnoreCase("q") && !newPassword.isEmpty()) {
			                setPassword(newPassword);
			            }
			            System.out.print("Enter new specializations (comma separated) (or press 'q' to quit): ");
			            String newSpecializationsInput = scanner.nextLine().trim();
			            if (!newSpecializationsInput.equalsIgnoreCase("q") && !newSpecializationsInput.isEmpty()) {
			                String[] newSpecializationsArray = newSpecializationsInput.split(",");
			                for (String specialization : newSpecializationsArray) {
			                    SpecializationType specializationType = SpecializationType.valueOf(specialization.trim().toUpperCase());
			                    if (!specializations.contains(specializationType)) {
			                        specializations.add(specializationType);
			                    }
			                }
			            }
			            System.out.print("Enter new cities (comma separated) (or press 'q' to quit): ");
			            String newCitiesInput = scanner.nextLine().trim();
			            if (!newCitiesInput.equalsIgnoreCase("q") && !newCitiesInput.isEmpty()) {
			                String[] newCitiesArray = newCitiesInput.split(",");
			                for (String city : newCitiesArray) {
			                    city = city.trim();
			                    if (!availableCities.contains(city)) {
			                        availableCities.add(city);
			                    }
			                }
			            }
			            usersCatalog.updateInstructor(this);
			            System.out.println("Account details have been successfully updated.");
			            break;
			        case 5:
			            System.out.println("\nLogging out...");
			            return;
			        default:
			            System.out.println("\nInvalid choice. Please enter a valid number between 1 and 5.");
			            break;
			    }
			}
		}
    }
}