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
        m.setIdEtudiant(rs.getInt("etudiant_id"));
        m.setValeur(rs.getDouble("valeur"));
        m.setSemestre(rs.getString("semestre"));
        m.setAnneeAcademique(rs.getString("annee_academique"));
        m.setMention(rs.getString("mention"));
        return m;
    } 
    public static String calculerMention(double valeur) {
        if (valeur >= 16) return "Très Bien";
        else if (valeur >= 14) return "Bien";
        else if (valeur >= 12) return "Assez Bien";
        else if (valeur >= 10) return "Passable";
        else return "Insuffisant";
    }

    public boolean recalculerEtSauvegarder(int etudiantId, String annee) {
    String sqlCalc = "SELECT AVG(note) as moyenne_calc FROM note WHERE etudiant_id = ?";
    double nouvelleValeur = 0;

    try (PreparedStatement psCalc = connection.prepareStatement(sqlCalc)) {
        psCalc.setInt(1, etudiantId);
        try (ResultSet rs = psCalc.executeQuery()) {
            if (rs.next()) {
                nouvelleValeur = rs.getDouble("moyenne_calc");
            }
        }

        String sqlCheck = "SELECT id FROM moyenneg WHERE etudiant_id = ?";
        Integer existingId = null;
        
        try (PreparedStatement psCheck = connection.prepareStatement(sqlCheck)) {
            psCheck.setInt(1, etudiantId);
            try (ResultSet rsCheck = psCheck.executeQuery()) {
                if (rsCheck.next()) existingId = rsCheck.getInt("id");
            }
        }

        if (existingId != null) {
            String sqlUpdate = "UPDATE moyenneg SET valeur = ?, mention = ?, annee_academique = ? WHERE id = ?";
            try (PreparedStatement psUp = connection.prepareStatement(sqlUpdate)) {
                psUp.setDouble(1, nouvelleValeur);
                psUp.setString(2, calculerMention(nouvelleValeur));
                psUp.setString(3, annee);
                psUp.setInt(4, existingId);
                return psUp.executeUpdate() > 0;
            }
        } else {
            Moyenneg m = new Moyenneg();
            m.setIdEtudiant(etudiantId);
            m.setValeur(nouvelleValeur);
            m.setAnneeAcademique(annee);
            return ajouter(m) != -1;
        }
    } catch (SQLException e) {
        System.err.println("Erreur recalculer: " + e.getMessage());
        return false;
    }
}
    public int ajouter(Moyenneg moyenne) {
        moyenne.setMention(calculerMention(moyenne.getValeur()));

        String sql = "INSERT INTO moyenneg (etudiant_id, valeur, semestre, annee_academique, mention) "
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

    public Moyenneg getMoyenneById(int id){
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

    public List<Moyenneg> getMoyennesByEtudiant(int etudiant_id) {
        List<Moyenneg> list = new ArrayList<>();
        String sql = "SELECT * FROM moyenneg WHERE etudiant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, etudiant_id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getMoyennesByEtudiant: " + e.getMessage());
        }
        return list;
    }

    public Moyenneg getMoyenneByEtudiantAndSemestre(int etudiant_id, String semestre) {
        String sql = "SELECT * FROM moyenneg WHERE etudiant_id = ? AND semestre = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, etudiant_id);
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
        moyenne.setMention(calculerMention(moyenne.getValeur()));

        String sql = "UPDATE moyenneg SET valeur = ?, semestre = ?, annee_academique = ?, "
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
        String sql = "SELECT * FROM moyenneg ORDER BY annee_academique, semestre";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Erreur getAllMoyennes: " + e.getMessage());
        }
        return list;
    }
}
