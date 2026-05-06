package controleur;

import DAO.EtudiantDAO;
import DAO.MatiereDAO;
import DAO.NoteDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import modele.Etudiant;
import modele.Matiere;
import modele.Note;

public class NoteControleur {
    private final NoteDAO noteDAO;
    private final EtudiantDAO etudiantDAO;
    private final MatiereDAO matiereDAO;

    public NoteControleur(Connection connection) throws SQLException {
        this.noteDAO = new NoteDAO(connection);
        this.etudiantDAO = new EtudiantDAO(connection);
        this.matiereDAO = new MatiereDAO(connection);
    }

    // =====================
    // AJOUTER
    // =====================
    public boolean ajouterNote(int idEtudiant, int idMatiere, double valeur,
                                String typeEvaluation, Date date) throws SQLException {

        // 1. Check note value is valid
        if (valeur < 0 || valeur > 20) {
            System.out.println("Erreur: la note doit être entre 0 et 20.");
            return false;
        }

        // 2. Check student exists
        Etudiant etudiant = etudiantDAO.getEtudiantById(idEtudiant);
        if (etudiant == null) {
            System.out.println("Erreur: étudiant introuvable.");
            return false;
        }

        // 3. Check subject exists
        Matiere matiere = matiereDAO.getMatiereById(idMatiere);
        if (matiere == null) {
            System.out.println("Erreur: matière introuvable.");
            return false;
        }

        // 4. Check typeEvaluation is valid
        if (typeEvaluation == null || typeEvaluation.isEmpty()) {
            System.out.println("Erreur: type d'évaluation obligatoire.");
            return false;
        }

        // 5. Build and save
        Note note = new Note();
        note.setIdEtudiant(idEtudiant);
        note.setIdMatiere(idMatiere);
        note.setNote(valeur);
        note.setTypeEvaluation(typeEvaluation);
        note.setDate_evaluation(date != null ? date : new Date()); // default to today

        int id = noteDAO.ajouter(note);
        if (id > 0) {
            System.out.println("Note ajoutée avec succès. ID: " + id);
            return true;
        }
        return false;
    }

    // =====================
    // MODIFIER
    // =====================
    public boolean modifierNote(int id, double valeur,
                                 String typeEvaluation, Date date) throws SQLException {

        // 1. Validate note value
        if (valeur < 0 || valeur > 20) {
            System.out.println("Erreur: la note doit être entre 0 et 20.");
            return false;
        }

        // 2. Build updated note
        Note note = new Note();
        note.setId(id);
        note.setNote(valeur);
        note.setTypeEvaluation(typeEvaluation);
        note.setDate_evaluation(date != null ? date : new Date());

        boolean result = noteDAO.modifier(note);
        if (result) System.out.println("Note modifiée avec succès.");
        else System.out.println("Erreur lors de la modification.");
        return result;
    }

    // =====================
    // SUPPRIMER
    // =====================
    public boolean supprimerNote(int id) throws SQLException {
        boolean result = noteDAO.supprimer(id);
        if (result) System.out.println("Note supprimée avec succès.");
        else System.out.println("Erreur: note introuvable.");
        return result;
    }

    // =====================
    // RECHERCHE & AFFICHAGE
    // =====================

    // All notes for a student
    public List<Note> getNotesByEtudiant(int idEtudiant) {
        Etudiant etudiant = etudiantDAO.getEtudiantById(idEtudiant);
        if (etudiant == null) {
            System.out.println("Erreur: étudiant introuvable.");
            return null;
        }
        List<Note> notes = noteDAO.getNotesByEtudiant(idEtudiant);
        if (notes.isEmpty()) System.out.println("Aucune note trouvée pour cet étudiant.");
        return notes;
    }

    // All notes for a subject
    public List<Note> getNotesByMatiere(int idMatiere) {
        Matiere matiere = matiereDAO.getMatiereById(idMatiere);
        if (matiere == null) {
            System.out.println("Erreur: matière introuvable.");
            return null;
        }
        List<Note> notes = noteDAO.getNotesByMatiere(idMatiere);
        if (notes.isEmpty()) System.out.println("Aucune note trouvée pour cette matière.");
        return notes;
    }

    // Notes for a student in a specific subject
    public List<Note> getNotesByEtudiantAndMatiere(int idEtudiant, int idMatiere) {
        List<Note> notes = noteDAO.getNotesByEtudiantAndMatiere(idEtudiant, idMatiere);
        if (notes.isEmpty()) System.out.println("Aucune note trouvée.");
        return notes;
    }

    // =====================
    // MOYENNE PAR MATIERE
    // =====================
    public double getMoyenneByEtudiantAndMatiere(int idEtudiant, int idMatiere) {
        // Check student exists
        if (etudiantDAO.getEtudiantById(idEtudiant) == null) {
            System.out.println("Erreur: étudiant introuvable.");
            return -1;
        }
        // Check subject exists
        if (matiereDAO.getMatiereById(idMatiere) == null) {
            System.out.println("Erreur: matière introuvable.");
            return -1;
        }
        double moyenne = noteDAO.getMoyenneByEtudiantAndMatiere(idEtudiant, idMatiere);
        System.out.printf("Moyenne de l'étudiant %d en matière %d: %.2f%n",
                idEtudiant, idMatiere, moyenne);
        return moyenne;
    }
}