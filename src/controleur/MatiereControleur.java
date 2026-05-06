package controleur;

import DAO.MatiereDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import modele.Matiere;

public class MatiereControleur {
    private final MatiereDAO matiereDAO;

    public MatiereControleur(Connection connection) throws SQLException {
        this.matiereDAO = new MatiereDAO(connection);
    }

    // =====================
    // AJOUTER
    // =====================
    public boolean ajouterMatiere(String nom, int coefficient,
                                   int volumeHoraire, String semestre) {

        // 1. Check if matiere already exists
        if (matiereDAO.getMatiereByNom(nom) != null) {
            System.out.println("Erreur: cette matière existe déjà.");
            return false;
        }

        // 2. Basic validation
        if (nom == null || nom.isEmpty()) {
            System.out.println("Erreur: le nom est obligatoire.");
            return false;
        }
        if (coefficient <= 0) {
            System.out.println("Erreur: le coefficient doit être supérieur à 0.");
            return false;
        }
        if (volumeHoraire <= 0) {
            System.out.println("Erreur: le volume horaire doit être supérieur à 0.");
            return false;
        }
        if (semestre == null || semestre.isEmpty()) {
            System.out.println("Erreur: le semestre est obligatoire.");
            return false;
        }

        // 3. Build and save
        Matiere matiere = new Matiere();
        matiere.setNom(nom);
        matiere.setCoefficient(coefficient);
        matiere.setVolumeHoraire(volumeHoraire);
        matiere.setSemestre(semestre);

        int id = matiereDAO.ajouter(matiere);
        if (id > 0) {
            System.out.println("Matière ajoutée avec succès. ID: " + id);
            return true;
        }
        return false;
    }

    // =====================
    // MODIFIER
    // =====================
    public boolean modifierMatiere(int id, String nom, int coefficient,
                                    int volumeHoraire, String semestre) throws SQLException {

        // 1. Check matiere exists
        Matiere matiere = matiereDAO.getMatiereById(id);
        if (matiere == null) {
            System.out.println("Erreur: matière introuvable.");
            return false;
        }

        // 2. Validate
        if (coefficient <= 0) {
            System.out.println("Erreur: le coefficient doit être supérieur à 0.");
            return false;
        }
        if (volumeHoraire <= 0) {
            System.out.println("Erreur: le volume horaire doit être supérieur à 0.");
            return false;
        }

        // 3. Update fields
        matiere.setNom(nom);
        matiere.setCoefficient(coefficient);
        matiere.setVolumeHoraire(volumeHoraire);
        matiere.setSemestre(semestre);

        boolean result = matiereDAO.modifier(matiere);
        if (result) System.out.println("Matière modifiée avec succès.");
        else System.out.println("Erreur lors de la modification.");
        return result;
    }

    // =====================
    // SUPPRIMER
    // =====================
    public boolean supprimerMatiere(int id) throws SQLException {
        Matiere matiere = matiereDAO.getMatiereById(id);
        if (matiere == null) {
            System.out.println("Erreur: matière introuvable.");
            return false;
        }
        boolean result = matiereDAO.supprimer(id);
        if (result) System.out.println("Matière supprimée avec succès.");
        else System.out.println("Erreur lors de la suppression.");
        return result;
    }

    // =====================
    // RECHERCHE & AFFICHAGE
    // =====================
    public List<Matiere> getAllMatieres() {
        List<Matiere> list = matiereDAO.getAllMatieres();
        if (list.isEmpty()) System.out.println("Aucune matière trouvée.");
        return list;
    }

    public Matiere getMatiereById(int id) {
        Matiere matiere = matiereDAO.getMatiereById(id);
        if (matiere == null) System.out.println("Matière introuvable.");
        return matiere;
    }

    public Matiere getMatiereByNom(String nom) {
        Matiere matiere = matiereDAO.getMatiereByNom(nom);
        if (matiere == null) System.out.println("Matière introuvable.");
        return matiere;
    }

    public List<Matiere> getMatieresBySemestre(String semestre) {
        List<Matiere> list = matiereDAO.getMatieresBySemestre(semestre);
        if (list.isEmpty()) System.out.println("Aucune matière trouvée pour ce semestre.");
        return list;
    }
}
