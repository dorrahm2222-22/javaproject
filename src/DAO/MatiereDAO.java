package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modele.Matiere;
import util.DBConnection;

public class MatiereDAO {
    private final Connection connection;

    public MatiereDAO(Connection connection)throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    private Matiere mapRow(ResultSet rs) throws SQLException {
        Matiere m = new Matiere();
        m.setId(rs.getInt("id"));
        m.setNom(rs.getString("nom"));
        m.setCoefficient(rs.getInt("coefficient"));
        m.setVolumeHoraire(rs.getInt("volumeHoraire"));
        m.setSemestre(rs.getString("semestre"));
        return m;
    }

    public int ajouter(Matiere matiere) {
        String sql = "INSERT INTO matiere (nom, coefficient, volumeHoraire, semestre) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, matiere.getNom());
            ps.setInt(2, matiere.getCoefficient());
            ps.setInt(3, matiere.getVolumeHoraire());
            ps.setString(4, matiere.getSemestre());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur ajouter matiere: " + e.getMessage());
        }
        return -1;
    }

    public List<Matiere> getAllMatieres() {
        List<Matiere> matieres = new ArrayList<>();
        String sql = "SELECT * FROM matiere ORDER BY nom";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) matieres.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Erreur getAllMatieres: " + e.getMessage());
        }
        return matieres;
    }

    public Matiere getMatiereById(int id) {
        String sql = "SELECT * FROM matiere WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getMatiereById: " + e.getMessage());
        }
        return null;
    }

    public Matiere getMatiereByNom(String nom) {
        String sql = "SELECT * FROM matiere WHERE nom = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getMatiereByNom: " + e.getMessage());
        }
        return null;
    }

    // Get all subjects for a specific semester
    public List<Matiere> getMatieresBySemestre(String semestre) {
        List<Matiere> matieres = new ArrayList<>();
        String sql = "SELECT * FROM matiere WHERE semestre = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, semestre);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) matieres.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getMatieresBySemestre: " + e.getMessage());
        }
        return matieres;
    }

    public boolean modifier(Matiere matiere) {
        String sql = "UPDATE matiere SET nom = ?, coefficient = ?, volumeHoraire = ?, "
                + "semestre = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, matiere.getNom());
            ps.setInt(2, matiere.getCoefficient());
            ps.setInt(3, matiere.getVolumeHoraire());
            ps.setString(4, matiere.getSemestre());
            ps.setInt(5, matiere.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modifier matiere: " + e.getMessage());
        }
        return false;
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM matiere WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur supprimer matiere: " + e.getMessage());
        }
        return false;
    }
}
