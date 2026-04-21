package util;

import java.sql.*;

public class ConnexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/systemedegestiondelecole";
    private static final String USER = "root";
    private static final String PASSWORD = "Eya.93230030";  

    private static Connection instance=null;    

    private ConnexionDB() {

        public static Connection getConnexxion() throws SQLException {
            if (instance == null || instance.isClosed()) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    instance = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Connexion MySQL établie avec succés!");
                } catch (ClassNotFoundException e) {
                    System.out.println("Driver MySQL non trouvé: " + e.getMessage());
                } catch (SQLException e) {
                    System.out.println("Erreur de connexion à la base de données: " + e.getMessage());
                    throw e;
                }
            }
            return instance;
        }
    } 
 
