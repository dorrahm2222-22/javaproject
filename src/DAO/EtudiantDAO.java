package DAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modele.Etudiant;




public class EtudiantDAO {
    private final Connection connection;

    public EtudiantDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Etudiant> getAllEtudiants() throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiant ORDER BY nom, prenom";
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
        etudiant.setNom(rs.getString("nom"));
        etudiant.setPrenom(rs.getString("prenom"));
        etudiant.setStatus(rs.getString("status"));
        etudiant.setIdEtudiant(rs.getString("idEtudiant"));
        etudiant.setActif(rs.getBoolean("actif"));
        if (rs.getDate("dateNaissance") != null) {
            etudiant.setDateNaissance(new java.util.Date(rs.getDate("dateNaissance").getTime()));
        }
        return etudiant;
    }

    public int ajouter(Etudiant etudiant) throws SQLException {
        String sql = "Insert into etudiant (login, motdepasse, email, nom, prenom , "
            + "dateNaissance, niveau, status, idEtudiant) "
            + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, etudiant.getLogin());
            ps.setString(2, etudiant.getMotDePasse());
            ps.setString(3, etudiant.getEmail());
            ps.setString(4, etudiant.getNom());
            ps.setString(5, etudiant.getPrenom());
            if (etudiant.getDateNaissance() != null) {
                ps.setDate(6, new java.sql.Date(etudiant.getDateNaissance().getTime()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            ps.setString(7, etudiant.getNiveau());
            ps.setString(8, etudiant.getStatus());
            ps.setString(9, etudiant.getIdEtudiant());
     
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        }

    }
     public boolean supprimer(int id) {
        String sql = "DELETE FROM etudiant WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur supprimer etudiant: " + e.getMessage());
        }
        return false;
    }
    public boolean modifier(Etudiant etudiant) {
        String sql = "UPDATE etudiant SET login = ?, motdepasse = ?, email = ?, nom = ?, prenom = ?, "
            + "dateNaissance = ?, niveau = ?, status = ?, idEtudiant = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, etudiant.getLogin());
            ps.setString(2, etudiant.getMotDePasse());
            ps.setString(3, etudiant.getEmail());
            ps.setString(4, etudiant.getNom());
            ps.setString(5, etudiant.getPrenom());
            if (etudiant.getDateNaissance() != null) {
                ps.setDate(6, new java.sql.Date(etudiant.getDateNaissance().getTime()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            ps.setString(7, etudiant.getNiveau());
            ps.setString(8, etudiant.getStatus());
            ps.setString(9, etudiant.getIdEtudiant());
            ps.setInt(10, etudiant.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modifier etudiant: " + e.getMessage());
        }
        return false;
        
    }

    public Etudiant getEtudiantById(int id) {
        String sql = "SELECT * FROM etudiant WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEtudiantById: " + e.getMessage());
        }
        return null;
    }

    public Etudiant getEtudiantByNom(String nom) {
        String sql = "SELECT * FROM etudiant WHERE nom = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEtudiantByNom: " + e.getMessage());
        }
        return null;
    }
    public List<Etudiant> getEtudiantsByNiveau(String niveau) {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiant WHERE niveau = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    etudiants.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEtudiantsByNiveau: " + e.getMessage());
        }
        return etudiants;
    }

    public Etudiant getEtudiantByLogin(String login) {
        String sql = "SELECT * FROM etudiant WHERE login = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEtudiantByLogin: " + e.getMessage());
        }
        return null;
    }

}







