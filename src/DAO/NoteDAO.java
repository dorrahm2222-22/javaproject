package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modele.Note;
import util.DBConnection;

public class NoteDAO {
    private final Connection connection;
    public NoteDAO(Connection connection) throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    private Note mapRow(ResultSet rs) throws SQLException {
        Note note = new Note();
        note.setId(rs.getInt("id"));
        note.setIdEtudiant(rs.getInt("idEtudiant"));
        note.setIdMatiere(rs.getInt("idMatiere"));
        note.setNote(rs.getDouble("note"));
        note.setTypeEvaluation(rs.getString("typeEvaluation"));
        if (rs.getDate("date_evaluation") != null) {
            note.setDate_evaluation(rs.getDate("date_evaluation"));
        }
        return note;
    }

    public int ajouter(Note note) {
        String sql = "INSERT INTO note (idEtudiant, idMatiere, note, typeEvaluation, date_evaluation) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, note.getIdEtudiant());
            ps.setInt(2, note.getIdMatiere());
            ps.setDouble(3, note.getNote());
            ps.setString(4, note.getTypeEvaluation());
            if (note.getDate_evaluation() != null) {
                ps.setDate(5, new java.sql.Date(note.getDate_evaluation().getTime()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur ajouter note: " + e.getMessage());
        }
        return -1;
    }
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM note ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) notes.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Erreur getAllNotes: " + e.getMessage());
        }
        return notes;
    }
    public Note getNoteById(int id) {
        String sql = "SELECT * FROM note WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getNoteById: " + e.getMessage());
        }
        return null;
    }

 // Get all notes for a specific student
    public List<Note> getNotesByEtudiant(int idEtudiant) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM note WHERE idEtudiant = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEtudiant);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) notes.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getNotesByEtudiant: " + e.getMessage());
        }
        return notes;
    }

  // Get all notes for a specific subject
    public List<Note> getNotesByMatiere(int idMatiere) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM note WHERE idMatiere = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idMatiere);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) notes.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getNotesByMatiere: " + e.getMessage());
        }
        return notes;
    }

   // Get all notes for a student in a specific subject
    public List<Note> getNotesByEtudiantAndMatiere(int idEtudiant, int idMatiere) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM note WHERE idEtudiant = ? AND idMatiere = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEtudiant);
            ps.setInt(2, idMatiere);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) notes.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getNotesByEtudiantAndMatiere: " + e.getMessage());
        }
        return notes;
    }

    public boolean modifier(Note note) {
        String sql = "UPDATE note SET idEtudiant = ?, idMatiere = ?, note = ?, "
                + "typeEvaluation = ?, date_evaluation = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, note.getIdEtudiant());
            ps.setInt(2, note.getIdMatiere());
            ps.setDouble(3, note.getNote());
            ps.setString(4, note.getTypeEvaluation());
            if (note.getDate_evaluation() != null) {
                ps.setDate(5, new java.sql.Date(note.getDate_evaluation().getTime()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            ps.setInt(6, note.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modifier note: " + e.getMessage());
        }
        return false;
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM note WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur supprimer note: " + e.getMessage());
        }
        return false;
    }

    // Calculate average for a student in a subject
    public double getMoyenneByEtudiantAndMatiere(int idEtudiant, int idMatiere) {
        String sql = "SELECT AVG(note) as moyenne FROM note WHERE idEtudiant = ? AND idMatiere = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEtudiant);
            ps.setInt(2, idMatiere);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("moyenne");
            }
        } catch (SQLException e) {
            System.err.println("Erreur getMoyenne: " + e.getMessage());
        }
        return 0.0;
    }






}