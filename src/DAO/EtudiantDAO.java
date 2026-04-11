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
        String sql = "SELECT * FROM etudiants ORDER BY nom, prenom";
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
        etudiant.setNom(rs.getString("nom"));
        etudiant.setPrenom(rs.getString("prenom"));
        return etudiant;
    }

    public int ajouter(Etudiant etudiant) throws SQLException {
        String sql = "Insert into etudiants (login, motdepasse, email, nom, prenom , "
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
            return ps.executeUpdate();
        }
    }
}




