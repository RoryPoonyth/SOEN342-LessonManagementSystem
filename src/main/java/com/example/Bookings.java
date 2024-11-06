package src.main.java.com.example;
import javax.persistence.*;

@Entity
@Table(name = "bookings")
public class Bookings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "minor_id", nullable = true)
    private Minor minor;

    @Column(name = "status", nullable = false)
    private String status = "Available";

    @Column(name = "active", nullable = false)
    private String active = "true";

    @Column(name = "is_cancelled", nullable = false)
    private boolean isCancelled = false;

    @ManyToOne
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering;

    public Bookings() {
    }

    public Bookings(Client client, String status, String active, boolean isCancelled, Offering offering) {
        this.client = client;
        this.status = status;
        this.active = active;
        this.isCancelled = isCancelled;
        this.offering = offering;
    }

    public int getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Minor getMinor() {
        return minor;
    }

    public String getStatus() {
        return status;
    }

    public String getActive() {
        return active;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public Offering getOffering() {
        return offering;
    }

    public void cancel() {
        this.isCancelled = true;
        if (this.offering != null) {
            this.offering.getBookings().remove(this);
        }
    }

    @Override
    public String toString() {
        if (isCancelled) {
            return String.format("Booking %d is %s for client %s in offering %s and is cancelled", id, status, client.getId(), offering.getId());
        }
        return String.format("Booking %d is %s for client %s in offering %s and is not cancelled", id, status, client.getId(), offering.getId());
    }

    public String reprClient() {
        return String.format(
            "Booking ID: %d\nOffering: %s\nLocation: %s, %s\nTimeslot: %s, %s - %s\nInstructor: %s\n",
            id,
            offering.getType().getValue(),
            offering.getLocation().getName(), offering.getLocation().getCity(),
            offering.getTimeslot().getDayOfWeek(), offering.getTimeslot().getStartTime(), offering.getTimeslot().getEndTime(),
            offering.getInstructor().getName()
        );
    }
}
