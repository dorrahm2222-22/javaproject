import java.sql.*;
public class App {
    public static void main(String[] args) throws Exception {

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/systemedegestiondelecole","root", "EVA.52975294");

            Statement stmt=con.createStatement();

            ResultSet rs=stmt.executeQuery("SELECT * FROM ?;");

            while(rs.next()){
                System.out.println();
            }
            con.close();
        }
        catch(Exception e){
            System.out.println(e);
        }

        
     }
}

