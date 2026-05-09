package modele;

import java.util.Date;

public class Etudiant extends Utilisateur {
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private String niveau;


    public Etudiant() {
        super();
        setRole("Etudiant");
    }

    public Etudiant(int id, String login, String motDePasse, String email,
                    String nom, String prenom, String niveau,String idEtudiant) {
        super(id,login, motDePasse, email, "Etudiant", true);
        this.nom = nom;
        this.prenom = prenom;
        this.niveau = niveau;
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


    public Date getDateNaissance() {
        return dateNaissance;
    }
    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    public int getIdEtudiant() {
        return super.getId();
    }

    @Override
    public String toString() {
        return "Etudiant [nom=" + nom + ", prenom=" + prenom + ", dateNaissance=" + dateNaissance + ", niveau=" + niveau
                + ", getId()=" + getId() + ", getLogin()=" + getLogin() + ", getNom()=" + getNom()
                + ", getMotDePasse()=" + getMotDePasse() + ", getPrenom()=" + getPrenom() + ", getEmail()=" + getEmail()
                + ", getNiveau()=" + getNiveau() + ", getRole()=" + getRole() + ", getDateNaissance()="
                + getDateNaissance() + ", isActif()=" + isActif() ;
    }

    

    
}