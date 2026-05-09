package controleur;

import DAO.AdminDAO;
import DAO.EnseignantDAO;
import DAO.EtudiantDAO;
import java.sql.Connection;
import java.sql.SQLException;
import modele.Admin;
import modele.Enseignant;
import modele.Etudiant;
import modele.Utilisateur;
    
public class AuthControleur {
    private final AdminDAO adminDAO;
    private final EnseignantDAO enseignantDAO;
    private final EtudiantDAO etudiantDAO;

    private Utilisateur utilisateurConnecte;

    public AuthControleur(Connection connection) throws SQLException {
        this.adminDAO = new AdminDAO(connection);
        this.enseignantDAO = new EnseignantDAO(connection);
        this.etudiantDAO = new EtudiantDAO(connection);
    }

    public Utilisateur connecter(String login, String motDePasse) throws SQLException {
        // 1. Check Admin table
        Admin admin = adminDAO.getAdminByLogin(login);
        if (admin != null && admin.getMotDePasse().equals(motDePasse)) {
            if (!admin.isActif()) {
                System.out.println("Compte désactivé.");
                return null;
            }
            utilisateurConnecte = admin;
            System.out.println("Connecté en tant qu'Admin: " + login);
            return admin;
        }

        // 2. Check Enseignant table
        Enseignant enseignant = enseignantDAO.getEnseignantByLogin(login);
        if (enseignant != null && enseignant.getMotDePasse().equals(motDePasse)) {
            if (!enseignant.isActif()) {
                System.out.println("Compte désactivé.");
                return null;
            }
            utilisateurConnecte = enseignant;
            System.out.println("Connecté en tant qu'Enseignant: " + login);
            return enseignant;
        }

        Etudiant etudiant = etudiantDAO.getEtudiantByLogin(login);
        if (etudiant != null && etudiant.getMotDePasse().equals(motDePasse)) {
            if (!etudiant.isActif()) {
                System.out.println("Compte désactivé.");
                return null;
            }
            utilisateurConnecte = etudiant;
            System.out.println("Connecté en tant qu'Etudiant: " + login);
            return etudiant;
        }

    
        System.out.println("Login ou mot de passe incorrect.");
        return null;
    }

  
    public void deconnecter() {
        System.out.println("Au revoir, " + utilisateurConnecte.getLogin() + "!");
        utilisateurConnecte = null;
    }


    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public boolean isConnecte() {
        return utilisateurConnecte != null;
    }

    public boolean isAdmin() {
        return utilisateurConnecte instanceof Admin;
    }

    public boolean isEnseignant() {
        return utilisateurConnecte instanceof Enseignant;
    }

    public boolean isEtudiant() {
        return utilisateurConnecte instanceof Etudiant;
    }

    public String getRole() {
        if (utilisateurConnecte == null) return null;
        return utilisateurConnecte.getRole();
    }
}