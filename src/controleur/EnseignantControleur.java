package controleur;

import DAO.EnseignantDAO;
import DAO.MatiereDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import modele.Enseignant;

public class EnseignantControleur {
    private final EnseignantDAO enseignantDAO;
    private final MatiereDAO matiereDAO;

    public EnseignantControleur(Connection connection)throws SQLException {
        this.enseignantDAO = new EnseignantDAO(connection);
        this.matiereDAO = new MatiereDAO(connection);
    }

    // =====================
    // AJOUTER
    // =====================
    public boolean ajouterEnseignant(String login, String motDePasse, String email,
                                      String nom, String prenom, String grade,
                                      String telephone, int matiereId) throws SQLException {

        // 1. Check if login already exists
        if (enseignantDAO.getEnseignantByLogin(login) != null) {
            System.out.println("Erreur: ce login existe déjà.");
            return false;
        }

        // 2. Check if matiere exists
        if (matiereDAO.getMatiereById(matiereId) == null) {
            System.out.println("Erreur: matière introuvable.");
            return false;
        }

        // 3. Basic validation
        if (login == null || login.isEmpty() ||
            motDePasse == null || motDePasse.isEmpty() ||
            nom == null || nom.isEmpty()) {
            System.out.println("Erreur: login, mot de passe et nom sont obligatoires.");
            return false;
        }

        // 4. Build and save
        Enseignant enseignant = new Enseignant();
        enseignant.setLogin(login);
        enseignant.setMotDePasse(motDePasse);
        enseignant.setEmail(email);
        enseignant.setNom(nom);
        enseignant.setPrenom(prenom);
        enseignant.setGrade(grade);
        enseignant.setTelephone(telephone);
        enseignant.setMatiereId(matiereId);
        enseignant.setActif(true);

        int id = enseignantDAO.ajouter(enseignant);
        if (id > 0) {
            System.out.println("Enseignant ajouté avec succès. ID: " + id);
            return true;
        }
        return false;
    }

    // =====================
    // MODIFIER
    // =====================
    public boolean modifierEnseignant(int id, String login, String email,
                                       String nom, String prenom, String grade,
                                       String telephone, int matiereId) throws SQLException {

        // 1. Check enseignant exists
        Enseignant enseignant = enseignantDAO.getEnseignantId(id);
        if (enseignant == null) {
            System.out.println("Erreur: enseignant introuvable.");
            return false;
        }

        // 2. Check matiere exists
        if (matiereDAO.getMatiereById(matiereId) == null) {
            System.out.println("Erreur: matière introuvable.");
            return false;
        }

        // 3. Update fields
        enseignant.setLogin(login);
        enseignant.setEmail(email);
        enseignant.setNom(nom);
        enseignant.setPrenom(prenom);
        enseignant.setGrade(grade);
        enseignant.setTelephone(telephone);
        enseignant.setMatiereId(matiereId);

        boolean result = enseignantDAO.modifier(enseignant);
        if (result) System.out.println("Enseignant modifié avec succès.");
        else System.out.println("Erreur lors de la modification.");
        return result;
    }

    // =====================
    // SUPPRIMER
    // =====================
    public boolean supprimerEnseignant(int id) throws SQLException {
        Enseignant enseignant = enseignantDAO.getEnseignantId(id);
        if (enseignant == null) {
            System.out.println("Erreur: enseignant introuvable.");
            return false;
        }
        boolean result = enseignantDAO.supprimer(id);
        if (result) System.out.println("Enseignant supprimé avec succès.");
        else System.out.println("Erreur lors de la suppression.");
        return result;
    }

    // =====================
    // RECHERCHE & AFFICHAGE
    // =====================
    public List<Enseignant> getAllEnseignants() throws SQLException {
        List<Enseignant> list = enseignantDAO.listerTous();
        if (list.isEmpty()) System.out.println("Aucun enseignant trouvé.");
        return list;
    }

}