package controleur;

import DAO.EtudiantDAO;
import DAO.MatiereDAO;
import DAO.MoyennegDAO;
import DAO.NoteDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import modele.Matiere;
import modele.Moyenneg;
import modele.Note;

public class MoyenneControleur {
    private final MoyennegDAO moyennegDAO;
    private final NoteDAO noteDAO;
    private final EtudiantDAO etudiantDAO;
    private final MatiereDAO matiereDAO;

    public MoyenneControleur(Connection connection) throws SQLException {
        this.moyennegDAO = new MoyennegDAO(connection);
        this.noteDAO = new NoteDAO(connection);
        this.etudiantDAO = new EtudiantDAO(connection);
        this.matiereDAO = new MatiereDAO(connection);
    }

    // =====================
    // CALCULER ET SAUVEGARDER
    // =====================
    public boolean calculerEtSauvegarderMoyenne(int idEtudiant,
                                                 String semestre,
                                                 String anneeAcademique) throws SQLException {

        // 1. Check student exists
        if (etudiantDAO.getEtudiantById(idEtudiant) == null) {
            System.out.println("Erreur: étudiant introuvable.");
            return false;
        }

        // 2. Get all subjects for this semester
        List<Matiere> matieres = matiereDAO.getMatieresBySemestre(semestre);
        if (matieres.isEmpty()) {
            System.out.println("Erreur: aucune matière trouvée pour ce semestre.");
            return false;
        }

        // 3. Calculate weighted average
        // Formula: sum(note * coefficient) / sum(coefficients)
        double sommeNotesPonderees = 0;
        int sommeCoefficients = 0;

        for (Matiere matiere : matieres) {
            List<Note> notes = noteDAO.getNotesByEtudiantAndMatiere(
                    idEtudiant, matiere.getId());

            if (!notes.isEmpty()) {
                // Average of all notes for this subject
                double moyenneMatiere = notes.stream()
                        .mapToDouble(Note::getNote)
                        .average()
                        .orElse(0);

                sommeNotesPonderees += moyenneMatiere * matiere.getCoefficient();
                sommeCoefficients += matiere.getCoefficient();
            }
        }

        // 4. Check if student has any notes at all
        if (sommeCoefficients == 0) {
            System.out.println("Erreur: aucune note trouvée pour cet étudiant ce semestre.");
            return false;
        }

        // 5. Compute final average
        double valeurMoyenne = sommeNotesPonderees / sommeCoefficients;

        // 6. Check if moyenne already exists for this student/semester
        Moyenneg existante = moyennegDAO.getMoyenneByEtudiantAndSemestre(
                idEtudiant, semestre);

        if (existante != null) {
            // Update existing
            existante.setValeur(valeurMoyenne);
            existante.setAnneeAcademique(anneeAcademique);
            boolean result = moyennegDAO.modifier(existante);
            if (result) {
                System.out.printf("Moyenne mise à jour: %.2f (%s)%n",
                        valeurMoyenne, MoyennegDAO.calculerMention(valeurMoyenne));
            }
            return result;
        } else {
            // Create new
            Moyenneg moyenne = new Moyenneg();
            moyenne.setIdEtudiant(idEtudiant);
            moyenne.setValeur(valeurMoyenne);
            moyenne.setSemestre(semestre);
            moyenne.setAnneeAcademique(anneeAcademique);

            int id = moyennegDAO.ajouter(moyenne);
            if (id > 0) {
                System.out.printf("Moyenne calculée et sauvegardée: %.2f (%s)%n",
                        valeurMoyenne, MoyennegDAO.calculerMention(valeurMoyenne));
                return true;
            }
            return false;
        }
    }

    // =====================
    // AFFICHAGE
    // =====================
    public List<Moyenneg> getMoyennesByEtudiant(int idEtudiant) throws SQLException {
        if (etudiantDAO.getEtudiantById(idEtudiant) == null) {
            System.out.println("Erreur: étudiant introuvable.");
            return null;
        }
        List<Moyenneg> list = moyennegDAO.getMoyennesByEtudiant(idEtudiant);
        if (list.isEmpty()) System.out.println("Aucune moyenne trouvée pour cet étudiant.");
        return list;
    }

    public Moyenneg getMoyenneByEtudiantAndSemestre(int idEtudiant, String semestre) throws SQLException {
        Moyenneg moyenne = moyennegDAO.getMoyenneByEtudiantAndSemestre(idEtudiant, semestre);
        if (moyenne == null) System.out.println("Aucune moyenne trouvée.");
        return moyenne;
    }

    public List<Moyenneg> getAllMoyennes() throws SQLException {
        List<Moyenneg> list = moyennegDAO.getAllMoyennes();
        if (list.isEmpty()) System.out.println("Aucune moyenne trouvée.");
        return list;
    }

    // =====================
    // SUPPRIMER
    // =====================
    public boolean supprimerMoyenne(int id) throws SQLException {
        boolean result = moyennegDAO.supprimer(id);
        if (result) System.out.println("Moyenne supprimée avec succès.");
        else System.out.println("Erreur: moyenne introuvable.");
        return result;
    }

    // =====================
    // STATISTIQUES
    // =====================

    // Best student average in a semester
    public Moyenneg getMeilleureMoyenne(String semestre) throws SQLException {
        List<Moyenneg> list = moyennegDAO.getAllMoyennes();
        return list.stream()
                .filter(m -> m.getSemestre().equals(semestre))
                .max((a, b) -> Double.compare(a.getValeur(), b.getValeur()))
                .orElse(null);
    }

    // Count how many students passed (moyenne >= 10)
    public long getNombreEtudiantsAdmis(String semestre) throws SQLException {
        List<Moyenneg> list = moyennegDAO.getAllMoyennes();
        return list.stream()
                .filter(m -> m.getSemestre().equals(semestre))
                .filter(m -> m.getValeur() >= 10)
                .count();
    }
}