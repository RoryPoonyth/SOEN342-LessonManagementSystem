package src.main.java.com.example;
import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "offerings")
public class Offering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OfferingType type;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = true)
    private Instructor instructor;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = false;

    @Column(name = "status", nullable = false)
    private String status = "Available";

    @OneToMany(mappedBy = "offering", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookings> bookings;

    @Enumerated(EnumType.STRING)
    @Column(name = "specialization", nullable = false)
    private SpecializationType specialization;

    @OneToOne(mappedBy = "offering", cascade = CascadeType.ALL, orphanRemoval = true)
    private Timeslot timeslot;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = true)
    private Location location;

    @Column(name = "capacity", nullable = true)
    private int capacity;

    @Column(name = "is_cancelled", nullable = false)
    private boolean isCancelled = false;

    public Offering(OfferingType type, SpecializationType specialization, Location location, int capacity, Timeslot timeslot) {
        this.type = type;
        this.specialization = specialization;
        this.location = location;
        this.capacity = capacity;
        this.timeslot = timeslot;
        this.status = "Not-Available";
        this.isAvailable = false;
        this.bookings = new java.util.ArrayList<>();
    }

    public Offering() {
    }

    public int getId() {
        return id;
    }

    public OfferingType getType() {
        return type;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Bookings> getBookings() {
        return bookings;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public Location getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity < bookings.size()) {
            throw new IllegalArgumentException("Capacity cannot be less than the number of bookings");
        }
        this.capacity = capacity;
    }

    public SpecializationType getSpecialization() {
        return specialization;
    }

    public void cancel() {
        this.isCancelled = true;
        this.location = null;
        this.status = "Not-Available";
        for (Bookings booking : bookings) {
            booking.cancel();
        }
    }

    public void cancelOffering() {
        this.status = "Not-Available";
        this.isAvailable = false;
        this.isCancelled = true;
        for (Bookings booking : bookings) {
            booking.cancel();
        }
    }

    public void addBooking(Bookings booking) {
        if (this.status.equals("Not-Available")) {
            throw new IllegalArgumentException("Offering is not available for booking");
        }
        this.bookings.add(booking);
    }

    public String reprClient() {
        return String.format(
            "Offering ID: %d\nType: %s\nLocation: %s, %s\nTimeslot: %s, %s - %s\nInstructor: %s\nSpecialization: %s\nCapacity: %d\nStatus: %s\n",
            id,
            type.getValue(),
            location.getName(), location.getCity(),
            timeslot.getDayOfWeek(), timeslot.getStartTime(), timeslot.getEndTime(),
            instructor != null ? instructor.getName() : "None",
            specialization,
            capacity,
            status
        );
    }

    @Override
    public String toString() {
        return String.format("Offering %d is a %s class with a capacity of %d and %d spots available at %s", id, type.name(), capacity, capacity - bookings.size(), location.getName());
    }
}

enum OfferingType {
    GROUP("group"),
    PRIVATE("private");

    private final String value;

    OfferingType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
