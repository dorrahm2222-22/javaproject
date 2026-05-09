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


    public boolean calculerEtSauvegarderMoyenne(int idEtudiant, String semestre,String anneeAcademique) throws SQLException {
    
        if (etudiantDAO.getEtudiantById(idEtudiant) == null) {
            System.out.println("Erreur: étudiant introuvable.");
            return false;
        }
        List<Matiere> matieres = matiereDAO.getMatieresBySemestre(semestre);
        if (matieres.isEmpty()) {
            System.out.println("Erreur: aucune matière trouvée pour ce semestre.");
            return false;
        }
        double sommeNotesPonderees = 0;
        int sommeCoefficients = 0;

        for (Matiere matiere : matieres) {
            List<Note> notes = noteDAO.getNotesByEtudiantAndMatiere(
                    idEtudiant, matiere.getId());

            if (!notes.isEmpty()) {
                double moyenneMatiere = notes.stream()
                        .mapToDouble(Note::getNote)
                        .average()
                        .orElse(0);

                sommeNotesPonderees += moyenneMatiere * matiere.getCoefficient();
                sommeCoefficients += matiere.getCoefficient();
            }
        }

        if (sommeCoefficients == 0) {
            System.out.println("Erreur: aucune note trouvée pour cet étudiant ce semestre.");
            return false;
        }

        double valeurMoyenne = sommeNotesPonderees / sommeCoefficients;

        Moyenneg existante = moyennegDAO.getMoyenneByEtudiantAndSemestre(
                idEtudiant, semestre);

        if (existante != null) {
            existante.setValeur(valeurMoyenne);
            existante.setAnneeAcademique(anneeAcademique);
            boolean result = moyennegDAO.modifier(existante);
            if (result) {
                System.out.printf("Moyenne mise à jour: %.2f (%s)%n",
                        valeurMoyenne, MoyennegDAO.calculerMention(valeurMoyenne));
            }
            return result;
        } else {
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

    public boolean supprimerMoyenne(int id) throws SQLException {
        boolean result = moyennegDAO.supprimer(id);
        if (result) System.out.println("Moyenne supprimée avec succès.");
        else System.out.println("Erreur: moyenne introuvable.");
        return result;
    }

    public long getNombreEtudiantsAdmis(String semestre) throws SQLException {
        List<Moyenneg> list = moyennegDAO.getAllMoyennes();
        return list.stream()
                .filter(m -> m.getSemestre().equals(semestre))
                .filter(m -> m.getValeur() >= 10)
                .count();
    }
}