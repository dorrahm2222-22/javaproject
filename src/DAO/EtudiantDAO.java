package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modele.Etudiant;
import util.DBConnection;

public class EtudiantDAO {
    private final Connection connection;

    public EtudiantDAO(Connection connection) throws SQLException {
        this.connection = DBConnection.getConnection();
    }

   
    public List<Etudiant> getAllEtudiants() throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT u.id, u.login, u.motdepasse, u.email, u.role, u.actif, "
                   + "e.nom, e.prenom, e.date_naissance, e.niveau "
                   + "FROM utilisateur u JOIN etudiant e ON u.id = e.id ORDER BY u.id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                etudiants.add(mapRow(rs));
            }
        }
        return etudiants;
    }


    private Etudiant mapRow(ResultSet rs) throws SQLException {
        Etudiant etudiant = new Etudiant();
        etudiant.setId(rs.getInt("id"));
        etudiant.setLogin(rs.getString("login"));
        etudiant.setMotDePasse(rs.getString("motdepasse"));
        etudiant.setEmail(rs.getString("email"));
        etudiant.setActif(rs.getBoolean("actif"));
        etudiant.setNom(rs.getString("nom"));
        etudiant.setPrenom(rs.getString("prenom"));
        etudiant.setNiveau(rs.getString("niveau"));
        if (rs.getDate("date_naissance") != null) {
            etudiant.setDateNaissance(new java.util.Date(rs.getDate("date_naissance").getTime()));
        }
        return etudiant;
    }


    public int ajouter(Etudiant etudiant) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String sqlUser = "INSERT INTO utilisateur (login, motdepasse, email, actif, role) "
                           + "VALUES (?, ?, ?, ?, 'Etudiant')";
            int userId = -1;

            try (PreparedStatement ps = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, etudiant.getLogin());
                ps.setString(2, etudiant.getMotDePasse());
                ps.setString(3, etudiant.getEmail());
                ps.setBoolean(4, etudiant.isActif());
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

   
            String sqlEtudiant = "INSERT INTO etudiant (id, nom, prenom, date_naissance, niveau) "
                               + "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement ps = connection.prepareStatement(sqlEtudiant)) {
                ps.setInt(1, userId);
                ps.setString(2, etudiant.getNom());
                ps.setString(3, etudiant.getPrenom());
                if (etudiant.getDateNaissance() != null) {
                    ps.setDate(4, new java.sql.Date(etudiant.getDateNaissance().getTime()));
                } else {
                    ps.setNull(4, java.sql.Types.DATE);
                }
                ps.setString(5, etudiant.getNiveau());
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

 
    public boolean supprimer(int id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur supprimer etudiant: " + e.getMessage());
        }
        return false;
    }

 
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean modifier(Etudiant etudiant) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String sqlUser = "UPDATE utilisateur SET login = ?, motdepasse = ?, email = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sqlUser)) {
                ps.setString(1, etudiant.getLogin());
                ps.setString(2, etudiant.getMotDePasse());
                ps.setString(3, etudiant.getEmail());
                ps.setInt(4, etudiant.getId());
                ps.executeUpdate();
            }

         
            String sqlEtudiant = "UPDATE etudiant SET nom = ?, prenom = ?, date_naissance = ?, " + "niveau = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sqlEtudiant)) {
                ps.setString(1, etudiant.getNom());
                ps.setString(2, etudiant.getPrenom());
                if (etudiant.getDateNaissance() != null) {
                    ps.setDate(3, new java.sql.Date(etudiant.getDateNaissance().getTime()));
                } else {
                    ps.setNull(3, java.sql.Types.DATE);
                }
                ps.setString(4, etudiant.getNiveau());
                ps.setInt(5, etudiant.getId());
                ps.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Erreur modifier etudiant: " + e.getMessage());
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public Etudiant getEtudiantById(int id) {
        String sql = "SELECT u.id, u.login, u.motdepasse, u.email, u.role, u.actif, "
                   + "e.nom, e.prenom, e.date_naissance, e.niveau "
                   + "FROM utilisateur u JOIN etudiant e ON u.id = e.id WHERE u.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEtudiantById: " + e.getMessage());
        }
        return null;
    }


    public Etudiant getEtudiantByLogin(String login) {
        String sql = "SELECT u.id, u.login, u.motdepasse, u.email, u.role, u.actif, "
                   + "e.nom, e.prenom, e.date_naissance, e.niveau "
                   + "FROM utilisateur u JOIN etudiant e ON u.id = e.id WHERE u.login = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEtudiantByLogin: " + e.getMessage());
        }
        return null;
    }

    public List<Etudiant> getEtudiantsByNiveau(String niveau) {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT u.id, u.login, u.motdepasse, u.email, u.role, u.actif, "
                   + "e.nom, e.prenom, e.date_naissance, e.niveau "
                   + "FROM utilisateur u JOIN etudiant e ON u.id = e.id WHERE e.niveau = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) etudiants.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEtudiantsByNiveau: " + e.getMessage());
        }
        return etudiants;
    }

  
    public Etudiant getEtudiantByNom(String nom) {
        String sql = "SELECT u.id, u.login, u.motdepasse, u.email, u.role, u.actif, "
                   + "e.nom, e.prenom, e.date_naissance, e.niveau "
                   + "FROM utilisateur u JOIN etudiant e ON u.id = e.id WHERE e.nom = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEtudiantByNom: " + e.getMessage());
        }
        return null;
    }
}