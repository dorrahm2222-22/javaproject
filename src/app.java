import java.sql.*;

public class App {
    public static void main(String[] args) {
        String url      = "jdbc:mysql://localhost:3306/systemedegestiondelecole";
        String user     = "root";
        String password = "EVA.52975294";

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            System.out.println(" Connected successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
