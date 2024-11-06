package src.main.java.com.example;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "minors")
public class Minor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age", nullable = false)
    private int age;

    @ManyToOne
    @JoinColumn(name = "guardian_id", nullable = false)
    private Client guardian;

    @OneToMany(mappedBy = "minor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookings> bookings;

    public Minor(Client guardian, String name, int age) {
        this.name = name;
        this.age = age;
        this.guardian = guardian;
    }

    public Minor() {
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Client getGuardian() {
        return guardian;
    }

    public void setGuardian(Client guardian) {
        this.guardian = guardian;
    }

    public List<Bookings> getBookings() {
        return bookings;
    }

    public void setBookings(List<Bookings> bookings) {
        this.bookings = bookings;
    }

    @Override
    public String toString() {
        return String.format("Minor %s is %d years old", name, age);
    }
}