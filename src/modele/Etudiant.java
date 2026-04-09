package modele;

import java.util.Date;

public class Etudiant extends Utilisateur {
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private String niveau;
    private String status;
    private String idEtudiant;


    public Etudiant() {
        super();
        setRole("Etudiant");
    }

    public Etudiant(int id, String login, String motDePasse, String email,
                    String nom, String prenom, String niveau) {
        super(id,login, motDePasse, email, "Etudiant", true);
        this.nom = nom;
        this.prenom = prenom;
        this.niveau = niveau;
        this.status = "Actif";
    }


    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    
    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }


    public String getNiveau() {
        return niveau;
    }
    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }


    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }


    public Date getDateNaissance() {
        return dateNaissance;
    }
    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }


    @Override
    public String toString() {
        return "Etudiant{" + "id=" + idEtudiant+ ", nom=" + nom + ", prenom=" + prenom + ", niveau=" + niveau + ", status=" + status + ", dateNaissance=" + dateNaissance + '}';
    }


}
