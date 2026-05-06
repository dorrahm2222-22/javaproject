package controleur;

import DAO.EtudiantDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import modele.Etudiant;

public class EtudiantControleur {
    private final EtudiantDAO etudiantDAO;

    public EtudiantControleur(Connection connection)throws SQLException {
        this.etudiantDAO = new EtudiantDAO(connection);
    }

    // =====================
    // AJOUTER
    // =====================
    public boolean ajouterEtudiant(String login, String motDePasse, String email,
                                    String nom, String prenom, String niveau,
                                    String idEtudiant, Date dateNaissance)throws SQLException {

        // 1. Check if login already exists
        if (etudiantDAO.getEtudiantByLogin(login) != null) {
            System.out.println("Erreur: ce login existe déjà.");
            return false;
        }

        // 2. Basic validation
        if (login == null || login.isEmpty() ||
            motDePasse == null || motDePasse.isEmpty() ||
            nom == null || nom.isEmpty()) {
            System.out.println("Erreur: login, mot de passe et nom sont obligatoires.");
            return false;
        }

        // 3. Build and save
        Etudiant etudiant = new Etudiant();
        etudiant.setLogin(login);
        etudiant.setMotDePasse(motDePasse);
        etudiant.setEmail(email);
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setNiveau(niveau);
        etudiant.setIdEtudiant(idEtudiant);
        etudiant.setDateNaissance(dateNaissance);
        etudiant.setStatus("Actif");
        etudiant.setActif(true);

        try {
            int id = etudiantDAO.ajouter(etudiant);
            if (id > 0) {
                System.out.println("Etudiant ajouté avec succès. ID: " + id);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur ajouterEtudiant: " + e.getMessage());
        }
        return false;
    }

    // =====================
    // MODIFIER
    // =====================
    public boolean modifierEtudiant(int id, String login, String motDePasse, String email,
                                     String nom, String prenom, String niveau,
                                     String idEtudiant, Date dateNaissance, String status)throws SQLException {

        // 1. Check student exists
        Etudiant etudiant = etudiantDAO.getEtudiantById(id);
        if (etudiant == null) {
            System.out.println("Erreur: étudiant introuvable.");
            return false;
        }

        // 2. Update fields
        etudiant.setLogin(login);
        etudiant.setMotDePasse(motDePasse);
        etudiant.setEmail(email);
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setNiveau(niveau);
        etudiant.setIdEtudiant(idEtudiant);
        etudiant.setDateNaissance(dateNaissance);
        etudiant.setStatus(status);

        boolean result = etudiantDAO.modifier(etudiant);
        if (result) System.out.println("Etudiant modifié avec succès.");
        else System.out.println("Erreur lors de la modification.");
        return result;
    }

    // =====================
    // SUPPRIMER
    // =====================
    public boolean supprimerEtudiant(int id) throws SQLException {
        Etudiant etudiant = etudiantDAO.getEtudiantById(id);
        if (etudiant == null) {
            System.out.println("Erreur: étudiant introuvable.");
            return false;
        }
        boolean result = etudiantDAO.supprimer(id);
        if (result) System.out.println("Etudiant supprimé avec succès.");
        else System.out.println("Erreur lors de la suppression.");
        return result;
    }

    // =====================
    // RECHERCHE & AFFICHAGE
    // =====================
    public List<Etudiant> getAllEtudiants() {
        try {
            return etudiantDAO.getAllEtudiants();
        } catch (SQLException e) {
            System.err.println("Erreur getAllEtudiants: " + e.getMessage());
            return null;
        }
    }

    public Etudiant getEtudiantById(int id) throws SQLException {
        Etudiant etudiant = etudiantDAO.getEtudiantById(id);
        if (etudiant == null) System.out.println("Etudiant introuvable.");
        return etudiant;
    }

    public Etudiant getEtudiantByLogin(String login) {
        Etudiant etudiant = etudiantDAO.getEtudiantByLogin(login);
        if (etudiant == null) System.out.println("Etudiant introuvable.");
        return etudiant;
    }

    public List<Etudiant> getEtudiantsByNiveau(String niveau) {
        List<Etudiant> list = etudiantDAO.getEtudiantsByNiveau(niveau);
        if (list.isEmpty()) System.out.println("Aucun étudiant trouvé pour ce niveau.");
        return list;
    }

    // =====================
    // ACTIVER / DESACTIVER
    // =====================
    public boolean activerEtudiant(int id) {
        Etudiant etudiant = etudiantDAO.getEtudiantById(id);
        if (etudiant == null) {
            System.out.println("Etudiant introuvable.");
            return false;
        }
        etudiant.setActif(true);
        etudiant.setStatus("Actif");
        return etudiantDAO.modifier(etudiant);
    }

    public boolean desactiverEtudiant(int id) {
        Etudiant etudiant = etudiantDAO.getEtudiantById(id);
        if (etudiant == null) {
            System.out.println("Etudiant introuvable.");
            return false;
        }
        etudiant.setActif(false);
        etudiant.setStatus("Inactif");
        return etudiantDAO.modifier(etudiant);
    }
}