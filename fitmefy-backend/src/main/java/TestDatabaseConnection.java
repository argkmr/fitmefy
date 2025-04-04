import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDatabaseConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5435/fitmefy_db"; // Adjust if running Dockerized Java app
        String username = "argkmr";
        String password = "argkmr-psql";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            if (connection != null) {
                System.out.println("✅ Connection to PostgreSQL successful!");
            } else {
                System.out.println("❌ Failed to make connection!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Connection failed! " + e.getMessage());
        }
    }
}
