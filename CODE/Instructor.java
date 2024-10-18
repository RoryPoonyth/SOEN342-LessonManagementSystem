import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Instructor {
    String name;
    String phoneNumber;
    Set<String> specializations;
    Set<String> availableCities;

    public Instructor(String name, String phoneNumber, Set<String> specializations, Set<String> availableCities) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.specializations = specializations;
        this.availableCities = availableCities;
    }

    public void saveToDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:lesson_booking.db")) {
            String sql = "INSERT INTO instructors (name, phone_number) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, phoneNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}