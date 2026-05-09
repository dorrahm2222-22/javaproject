package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modele.Enseignant;
import util.DBConnection;

public class EnseignantDAO {

    private final Connection connection;

    public EnseignantDAO(Connection connection) throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    private Enseignant mapRow(ResultSet rs) throws SQLException {
        Enseignant e = new Enseignant();
        e.setId(rs.getInt("id"));
        e.setLogin(rs.getString("login"));
        e.setMotDePasse(rs.getString("motdepasse"));
        e.setEmail(rs.getString("email"));
        e.setActif(rs.getBoolean("actif"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        e.setTelephone(rs.getString("telephone"));
        e.setMatiereId(rs.getInt("matiere_id"));
        return e;
    }

    public int ajouter(Enseignant enseignant) throws SQLException {
        connection.setAutoCommit(false);
        try {
         
            String sqlUser = "INSERT INTO utilisateur (login, motdepasse, email, actif, role) "
                           + "VALUES (?, ?, ?, ?, 'Enseignant')";
            int userId = -1;

            try (PreparedStatement ps = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, enseignant.getLogin());
                ps.setString(2, enseignant.getMotDePasse());
                ps.setString(3, enseignant.getEmail());
                ps.setBoolean(4, enseignant.isActif());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        userId = keys.getInt(1);
                    }
                }
            }

            if (userId == -1) {
                connection.rollback();
                return -1;
            }

            String sqlEnseignant = "INSERT INTO enseignant (id, nom, prenom, telephone, matiere_id) " + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sqlEnseignant)) {
                ps.setInt(1, userId);
                ps.setString(2, enseignant.getNom());
                ps.setString(3, enseignant.getPrenom());
                ps.setString(4, enseignant.getTelephone());
                ps.setInt(5, enseignant.getMatiereId());
                ps.executeUpdate();
            }

            connection.commit();
            return userId;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public List<Enseignant> listerTous() {
        List<Enseignant> liste = new ArrayList<>();
        String sql = "SELECT u.id, u.login, u.motdepasse, u.email, u.actif, "
                   + "e.nom, e.prenom, e.telephone, e.matiere_id "
                   + "FROM utilisateur u JOIN enseignant e ON u.id = e.id "
                   + "ORDER BY e.nom";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur listerTous enseignants: " + e.getMessage());
        }
        return liste;
    }


    public Enseignant getEnseignantById(int id) {
        String sql = "SELECT u.id, u.login, u.motdepasse, u.email, u.actif, "
                   + "e.nom, e.prenom, e.telephone, e.matiere_id "
                   + "FROM utilisateur u JOIN enseignant e ON u.id = e.id WHERE u.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEnseignantById: " + e.getMessage());
        }
        return null;
    }

   
    public Enseignant getEnseignantByLogin(String login) {
        String sql = "SELECT u.id, u.login, u.motdepasse, u.email, u.actif, "
                   + "e.nom, e.prenom, e.telephone, e.matiere_id "
                   + "FROM utilisateur u JOIN enseignant e ON u.id = e.id WHERE u.login = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEnseignantByLogin: " + e.getMessage());
        }
        return null;
    }


    public Enseignant getEnseignantByNom(String nom) {
        String sql = "SELECT u.id, u.login, u.motdepasse, u.email, u.actif, "
                   + "e.nom, e.prenom, e.telephone, e.matiere_id "
                   + "FROM utilisateur u JOIN enseignant e ON u.id = e.id WHERE e.nom = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEnseignantByNom: " + e.getMessage());
        }
        return null;
    }

    public Enseignant getEnseignantByMatiereId(int matiereId) {
        String sql = "SELECT u.id, u.login, u.motdepasse, u.email, u.actif, "
                   + "e.nom, e.prenom, e.telephone, e.matiere_id "
                   + "FROM utilisateur u JOIN enseignant e ON u.id = e.id WHERE e.matiere_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matiereId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEnseignantByMatiereId: " + e.getMessage());
        }
        return null;
    }

   
    public boolean modifier(Enseignant enseignant) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String sqlUser = "UPDATE utilisateur SET login = ?, motdepasse = ?, email = ?, actif = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sqlUser)) {
                ps.setString(1, enseignant.getLogin());
                ps.setString(2, enseignant.getMotDePasse());
                ps.setString(3, enseignant.getEmail());
                ps.setBoolean(4, enseignant.isActif());
                ps.setInt(5, enseignant.getId());
                ps.executeUpdate();
            }

            String sqlEnseignant = "UPDATE enseignant SET nom = ?, prenom = ?, telephone = ?, "
                                 + "matiere_id = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sqlEnseignant)) {
                ps.setString(1, enseignant.getNom());
                ps.setString(2, enseignant.getPrenom());
                ps.setString(3, enseignant.getTelephone());
                ps.setInt(4, enseignant.getMatiereId());
                ps.setInt(5, enseignant.getId());
                ps.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Erreur modifier enseignant: " + e.getMessage());
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }


    public boolean supprimer(int id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur supprimer enseignant: " + e.getMessage());
        }
        return false;
    }
}