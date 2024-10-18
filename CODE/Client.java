import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Client {
    String name;
    int age;
    boolean hasGuardian;
    String guardianName;

    public Client(String name, int age, String guardianName) {
        this.name = name;
        this.age = age;
        if (age < 18) {
            this.hasGuardian = true;
            this.guardianName = guardianName;
        } else {
            this.hasGuardian = false;
            this.guardianName = "";
        }
    }

    public void saveToDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:lesson_booking.db")) {
            String sql = "INSERT INTO clients (name, age, guardian_name) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, guardianName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
