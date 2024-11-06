package src.main.java.com.example;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "timeslots")
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @OneToOne(mappedBy = "timeslot", cascade = CascadeType.ALL, orphanRemoval = true)
    private Offering offering;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    public Timeslot(String dayOfWeek, LocalTime startTime, LocalDate startDate, LocalTime endTime, LocalDate endDate, Schedule schedule) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.startDate = startDate;
        this.endTime = endTime;
        this.endDate = endDate;
        this.schedule = schedule;
    }

    public Timeslot() {
    }

    public int getId() {
        return id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Offering getOffering() {
        return offering;
    }

    public void setOffering(Offering offering) {
        this.offering = offering;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return String.format("Timeslot %d is on %s from %s to %s starting on %s and ending on %s for Offering %d", id, dayOfWeek, startTime, endTime, startDate, endDate, offering != null ? offering.getId() : -1);
    }

    public boolean overlapsWith(Timeslot other) {
        LocalDateTime thisStartDateTime = LocalDateTime.of(this.startDate, this.startTime);
        LocalDateTime thisEndDateTime = LocalDateTime.of(this.endDate, this.endTime);
        LocalDateTime otherStartDateTime = LocalDateTime.of(other.getStartDate(), other.getStartTime());
        LocalDateTime otherEndDateTime = LocalDateTime.of(other.getEndDate(), other.getEndTime());

        return !thisStartDateTime.isAfter(otherEndDateTime) && !thisEndDateTime.isBefore(otherStartDateTime);
    }

    public boolean isConflicting(List<Timeslot> bookedTimeslots) {
        LocalDateTime newStartDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime newEndDateTime = LocalDateTime.of(endDate, endTime);

        for (Timeslot bookedTimeslot : bookedTimeslots) {
            LocalDateTime bookedStartDateTime = LocalDateTime.of(bookedTimeslot.getStartDate(), bookedTimeslot.getStartTime());
            LocalDateTime bookedEndDateTime = LocalDateTime.of(bookedTimeslot.getEndDate(), bookedTimeslot.getEndTime());

            if (!newStartDateTime.isAfter(bookedEndDateTime) && !newEndDateTime.isBefore(bookedStartDateTime)) {
                if (bookedTimeslot.getDayOfWeek().equals(this.dayOfWeek)) {
                    if ((bookedTimeslot.getStartTime().isBefore(this.endTime) && this.startTime.isBefore(bookedTimeslot.getEndTime())) ||
                            (this.startTime.isBefore(bookedTimeslot.getStartTime()) && bookedTimeslot.getEndTime().isBefore(this.endTime))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
