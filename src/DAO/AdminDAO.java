package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modele.Admin;
import util.DBConnection;

public class AdminDAO {
    private final Connection connection;    

    public AdminDAO(Connection connection)throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    private Admin mapRow(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getInt("id"));
        admin.setLogin(rs.getString("login"));
        admin.setMotDePasse(rs.getString("motdepasse"));
        admin.setEmail(rs.getString("email"));
        admin.setActif(rs.getBoolean("actif"));
        return admin;
    }
public int ajouter(Admin admin) throws SQLException {
    String sql = "INSERT INTO admin (login, motdepasse, email, actif) VALUES (?, ?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, admin.getLogin());
        ps.setString(2, admin.getMotDePasse());
        ps.setString(3, admin.getEmail());
        ps.setBoolean(4, admin.isActif());
        ps.executeUpdate();
        try (ResultSet keys = ps.getGeneratedKeys()) {
            if (keys.next()) return keys.getInt(1);
        }
    } catch (SQLException e) {
        System.err.println("Erreur ajouter admin: " + e.getMessage());
    }
    return -1;
}


public List<Admin> lister() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM admin";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                admins.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lister admins: " + e.getMessage());
        }
        return admins;
    }


public Admin getAdminById(int id){
        String sql = "SELECT * FROM admin WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAdminById: " + e.getMessage());
        }
        return null;

}
    public Admin getAdminByLogin(String login) {
        String sql = "SELECT * FROM admin WHERE login = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAdminByLogin: " + e.getMessage());
        }
        return null;
    }

    public boolean modifier(Admin admin) {
        String sql = "UPDATE admin SET login = ?, motdepasse = ?, email = ?, actif = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, admin.getLogin());
            ps.setString(2, admin.getMotDePasse());
            ps.setString(3, admin.getEmail());
            ps.setBoolean(4, admin.isActif());
            ps.setInt(5, admin.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modifier admin: " + e.getMessage());
        }
        return false;
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM admin WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur supprimer admin: " + e.getMessage());
        }
        return false;
    }
}
































