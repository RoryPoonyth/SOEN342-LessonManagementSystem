import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Location {
    String name;
    String city;
    String type;
    List<TimeSlot> availableSlots;

    public Location(String name, String city, String type) {
        this.name = name;
        this.city = city;
        this.type = type;
        this.availableSlots = new ArrayList<>();
    }

    public void saveToDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:lesson_booking.db")) {
            String sql = "INSERT INTO locations (name, city, type) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, city);
            pstmt.setString(3, type);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}