package DAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modele.Enseignant;

public class EnseignantDAO {
 
    private Connection connexion;
 
    public EnseignantDAO(Connection connexion) {
        this.connexion = connexion;
    }
 
    public int ajouter(Enseignant enseignant) {
        String sql = "INSERT INTO enseignant (login, mot_de_passe, email, nom, prenom, " +
                     "telephone, grade, matiere_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, enseignant.getLogin());
            ps.setString(2, enseignant.getMotDePasse());
            ps.setString(3, enseignant.getEmail());
            ps.setString(4, enseignant.getNom());
            ps.setString(5, enseignant.getPrenom());
            ps.setString(6, enseignant.getTelephone());
            ps.setString(7, enseignant.getGrade());
            ps.setInt(8, enseignant.getMatiereId());
 
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur ajouter enseignant: " + e.getMessage());
        }
        return -1;
    }
 
    public List<Enseignant> listerTous() {
        List<Enseignant> liste = new ArrayList<>();
        String sql = "SELECT e.*, m.nom AS matiere_nom FROM enseignant e " +
                     "LEFT JOIN matiere m ON e.matiere_id = m.id ORDER BY e.nom";
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur listerTous enseignants: " + e.getMessage());
        }
        return liste;
    }
 
    public Enseignant getEnseignantId(int id) {
        String sql = "SELECT * FROM enseignant WHERE id = ?";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Erreur getEnseignantId enseignant: " + e.getMessage());
        }
        return null;
    }
    public Enseignant getEnseignantnom(String nom) {
        String sql = "SELECT * FROM enseignant WHERE nom = ?";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Erreur getEnseignantnom enseignant: " + e.getMessage());
        }
        return null;
    }
    public Enseignant getEnseignantmatiereId(int matiereId) {
        String sql = "SELECT * FROM enseignant WHERE matiere_id = ?";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1, matiereId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Erreur getEnseignantmatiereId enseignant: " + e.getMessage());
        }
        return null;
    }
 
 
    private Enseignant mapRow(ResultSet rs) throws SQLException {
        Enseignant e = new Enseignant();
        e.setId(rs.getInt("id"));
        e.setLogin(rs.getString("login"));
        e.setEmail(rs.getString("email"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        e.setTelephone(rs.getString("telephone"));
        e.setGrade(rs.getString("grade"));
        e.setMatiereId(rs.getInt("matiere_id"));
        e.setActif(rs.getBoolean("actif"));
        return e;
    }
       public boolean modifier(Enseignant enseignant) {
        String sql = "UPDATE enseignant SET login=?, email=?, nom=?, prenom=?, " +
                     "telephone=?, grade=?, matiere_id=? WHERE id=?";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setString(1, enseignant.getLogin());
            ps.setString(2, enseignant.getEmail());
            ps.setString(3, enseignant.getNom());
            ps.setString(4, enseignant.getPrenom());
            ps.setString(5, enseignant.getTelephone());
            ps.setString(6, enseignant.getGrade());
            ps.setInt(7, enseignant.getMatiereId());
            ps.setInt(8, enseignant.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modifier enseignant: " + e.getMessage());
        }
        return false;
    }
 
    public boolean supprimer(int id) {
        String sql = "DELETE FROM enseignant WHERE id = ?";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur supprimer enseignant: " + e.getMessage());
        }
        return false;
    }
}