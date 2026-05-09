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
    String sql1 = "INSERT INTO utilisateur (login, motdepasse, email, actif, role) VALUES ( ?, ?, ?, ?, 'admin')";
    try (PreparedStatement ps = connection.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, admin.getLogin());
        ps.setString(2, admin.getMotDePasse());
        ps.setString(3, admin.getEmail());
        ps.setBoolean(4, admin.isActif());
        ps.executeUpdate();
        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            int id = keys.getInt(1);
            String sql2 = "INSERT INTO admin (id) VALUES (?)";
            try (PreparedStatement ps2 = connection.prepareStatement(sql2)) {
                ps2.setInt(1, id);
                ps2.executeUpdate();
            }
            return id;
        }
    }
    return -1;
}


public List<Admin> lister() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT u.* FROM utilisateur u JOIN admin a ON u.id = a.id ORDER BY u.id";
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
        String sql = "SELECT u.* FROM utilisateur u JOIN admin a ON u.id = a.id WHERE u.id = ?";
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
        String sql = "SELECT u.* FROM utilisateur u JOIN admin a ON u.id = a.id WHERE u.login = ?";
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
        String sql = "UPDATE utilisateur SET login = ?, motdepasse = ?, email = ?, actif = ? WHERE id = ?";
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
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur supprimer admin: " + e.getMessage());
        }
        return false;
    }
}
































