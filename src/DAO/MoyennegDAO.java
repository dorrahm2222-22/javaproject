package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modele.Moyenneg;
import util.DBConnection;
public class MoyennegDAO {
    private final Connection connection;
 
    public MoyennegDAO(Connection connection) throws SQLException {
        this.connection = DBConnection.getConnection();
    }


    private Moyenneg mapRow(ResultSet rs) throws SQLException {
        Moyenneg m = new Moyenneg();
        m.setId(rs.getInt("id"));
        m.setIdEtudiant(rs.getInt("idEtudiant"));
        m.setValeur(rs.getDouble("valeur"));
        m.setSemestre(rs.getString("semestre"));
        m.setAnneeAcademique(rs.getString("anneeAcademique"));
        m.setMention(rs.getString("mention"));
        return m;
    }  
    // Automatically calculate mention based on grade value
    public static String calculerMention(double valeur) {
        if (valeur >= 16) return "Très Bien";
        else if (valeur >= 14) return "Bien";
        else if (valeur >= 12) return "Assez Bien";
        else if (valeur >= 10) return "Passable";
        else return "Insuffisant";
    }

    public int ajouter(Moyenneg moyenne) {
        // Auto-calculate mention before saving
        moyenne.setMention(calculerMention(moyenne.getValeur()));

        String sql = "INSERT INTO moyenneg (idEtudiant, valeur, semestre, anneeAcademique, mention) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, moyenne.getIdEtudiant());
            ps.setDouble(2, moyenne.getValeur());
            ps.setString(3, moyenne.getSemestre());
            ps.setString(4, moyenne.getAnneeAcademique());
            ps.setString(5, moyenne.getMention());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur ajouter moyenneg: " + e.getMessage());
        }
        return -1;
    }
  
    public Moyenneg getMoyenneById(int id) {
        String sql = "SELECT * FROM moyenneg WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getMoyenneById: " + e.getMessage());
        }
        return null;
    }
    // Get all averages for a student
    public List<Moyenneg> getMoyennesByEtudiant(int idEtudiant) {
        List<Moyenneg> list = new ArrayList<>();
        String sql = "SELECT * FROM moyenneg WHERE idEtudiant = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEtudiant);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getMoyennesByEtudiant: " + e.getMessage());
        }
        return list;
    }

    // Get average for a student in a specific semester
    public Moyenneg getMoyenneByEtudiantAndSemestre(int idEtudiant, String semestre) {
        String sql = "SELECT * FROM moyenneg WHERE idEtudiant = ? AND semestre = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEtudiant);
            ps.setString(2, semestre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getMoyenneByEtudiantAndSemestre: " + e.getMessage());
        }
        return null;
    }

    public boolean modifier(Moyenneg moyenne) {
        // Recalculate mention on update too
        moyenne.setMention(calculerMention(moyenne.getValeur()));

        String sql = "UPDATE moyenneg SET valeur = ?, semestre = ?, anneeAcademique = ?, "
                + "mention = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, moyenne.getValeur());
            ps.setString(2, moyenne.getSemestre());
            ps.setString(3, moyenne.getAnneeAcademique());
            ps.setString(4, moyenne.getMention());
            ps.setInt(5, moyenne.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modifier moyenneg: " + e.getMessage());
        }
        return false;
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM moyenneg WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur supprimer moyenneg: " + e.getMessage());
        }
        return false;
    }

    public List<Moyenneg> getAllMoyennes() {
        List<Moyenneg> list = new ArrayList<>();
        String sql = "SELECT * FROM moyenneg ORDER BY anneeAcademique, semestre";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Erreur getAllMoyennes: " + e.getMessage());
        }
        return list;
    }
}
