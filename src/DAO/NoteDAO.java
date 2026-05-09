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
        note.setIdEtudiant(rs.getInt("etudiant_id"));
        note.setIdMatiere(rs.getInt("matiere_id"));
        note.setNote(rs.getDouble("note"));
        note.setTypeEvaluation(rs.getString("type_evaluation"));
        if (rs.getDate("date_evaluation") != null) {
            note.setDate_evaluation(rs.getDate("date_evaluation"));
        }
        return note;
    }

    public int ajouter(Note note) {
        String sql = "INSERT INTO note (etudiant_id,matiere_id, note, type_evaluation, date_evaluation) "
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


    public List<Note> getNotesByEtudiant(int etudiant_id) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM note WHERE etudiant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, etudiant_id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) notes.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getNotesByEtudiant: " + e.getMessage());
        }
        return notes;
    }

  // Get all notes for a specific subject
    public List<Note> getNotesByMatiere(int matiere_id) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM note WHERE matiere_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matiere_id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) notes.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getNotesByMatiere: " + e.getMessage());
        }
        return notes;
    }

    public List<Note> getNotesByEtudiantAndMatiere(int etudiant_id, int matiere_id) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM note WHERE etudiant_id = ? AND matiere_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, etudiant_id);
            ps.setInt(2, matiere_id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) notes.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getNotesByEtudiantAndMatiere: " + e.getMessage());
        }
        return notes;
    }

    public boolean modifier(Note note) {
        String sql = "UPDATE note SET etudiant_id = ?, matiere_id = ?, note = ?, "
                + "type_evaluation = ?, date_evaluation = ? WHERE id = ?";
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

    public double getMoyenneByEtudiantAndMatiere(int etudiant_id, int matiere_id) {
        String sql = "SELECT AVG(note) as moyenne FROM note WHERE etudiant_id = ? AND matiere_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, etudiant_id);
            ps.setInt(2, matiere_id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("moyenne");
            }
        } catch (SQLException e) {
            System.err.println("Erreur getMoyenne: " + e.getMessage());
        }
        return 0.0;
    }






}