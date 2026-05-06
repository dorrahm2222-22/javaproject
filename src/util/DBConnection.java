package util;
import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/systemedegestiondelecole";
    private static final String USER = "root";
    private static final String PASSWORD = "123456789";
    private static Connection instance;

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return instance;
    }
}