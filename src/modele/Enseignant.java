package modele;
public class Enseignant extends Utilisateur {
    private String nom;
    private String prenom;
    private String grade;
    private String telephone;
    private int matiereId;

    public Enseignant() {
        super();
        setRole("Enseignant");
    }

    public Enseignant(int id, String login, String motDePasse, String email,
                       String nom, String prenom, String grade, String telephone, int matiereId) {
        super(id, login, motDePasse, email, "Enseignant", true);
        this.nom = nom;
        this.prenom = prenom;
        this.grade = grade;
        this.telephone = telephone;
        this.matiereId = matiereId;
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


    public String getGrade() {
        return grade;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }


    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    
    public int getMatiereId() {
        return matiereId;
    }
    public void setMatiereId(int matiereId) {
        this.matiereId = matiereId;
    }

    @Override
    public String toString() {
        return "Enseignant [nom=" + nom + ", prenom=" + prenom + ", grade=" + grade + ", telephone=" + telephone
                + ", matiereId=" + matiereId + "]";
    }


    
    
}
