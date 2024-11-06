package src.main.java.com.example;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ElementCollection
    @CollectionTable(name = "location_space_types", joinColumns = @JoinColumn(name = "location_id"))
    @Column(name = "space_type", nullable = false)
    private List<String> spaceTypes;

    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private Schedule schedule;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Offering> offerings;

    public Location(String name, String address, int capacity, City city, List<String> spaceTypes) {
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.city = city;
        this.spaceTypes = spaceTypes;
        this.schedule = new Schedule(this);
    }

    public Location() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<String> getSpaceTypes() {
        return spaceTypes;
    }

    public void setSpaceTypes(List<String> spaceTypes) {
        this.spaceTypes = spaceTypes;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public List<Offering> getOfferings() {
        return offerings;
    }

    public void setOfferings(List<Offering> offerings) {
        this.offerings = offerings;
    }

    public void delete() {
        if (schedule != null) {
            for (Offering offering : offerings) {
                offering.cancel();
            }
            schedule.clearTimeslots();
        }
        this.schedule = null;
    }

    @Override
    public String toString() {
        return String.format("Location %d %s (%s), has a capacity of %d and is located in %s and has %s space type(s)", id, name, address, capacity, city.getName(), spaceTypes);
    }
}
