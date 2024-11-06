package src.main.java.com.example;
import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Timeslot> timeslots = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    public Schedule(Location location) {
        this.location = location;
        this.timeslots = new ArrayList<>();
    }

    public Schedule() {
    }

    public int getId() {
        return id;
    }

    public List<Timeslot> getTimeslots() {
        return timeslots;
    }

    public void addTimeslot(Timeslot timeslot) {
        this.timeslots.add(timeslot);
    }

    public void removeTimeslot(Timeslot timeslot) {
        this.timeslots.remove(timeslot);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    // Method to clear all timeslots associated with the schedule
    public void clearTimeslots() {
        for (Timeslot timeslot : timeslots) {
            timeslot.setSchedule(null); // Break association
        }
        timeslots.clear();
    }

    public void delete() {
        this.timeslots.clear();
    }

    public boolean isConflicting(Timeslot timeslot) {
        for (Timeslot ts : timeslots) {
            LocalDate tsStartDate = ts.getStartDate();
            LocalDate tsEndDate = ts.getEndDate();
            LocalTime tsStartTime = ts.getStartTime();
            LocalTime tsEndTime = ts.getEndTime();

            LocalDate timeslotStartDate = timeslot.getStartDate();
            LocalDate timeslotEndDate = timeslot.getEndDate();
            LocalTime timeslotStartTime = timeslot.getStartTime();
            LocalTime timeslotEndTime = timeslot.getEndTime();

            if (!timeslotStartDate.isAfter(tsEndDate) && !timeslotEndDate.isBefore(tsStartDate)) {
                if (ts.getDayOfWeek().equals(timeslot.getDayOfWeek())) {
                    if ((tsStartTime.isBefore(timeslotEndTime) && timeslotStartTime.isBefore(tsEndTime)) ||
                            (timeslotStartTime.isBefore(tsStartTime) && tsEndTime.isBefore(timeslotEndTime))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Schedule %d for %s has %d timeslots", id, location.getName(), timeslots.size());
    }
}
