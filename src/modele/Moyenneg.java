package modele;

public class Moyenneg {
    private int id;
    private int idEtudiant;
    private double valeur;
    private String semestre;
    private String anneeAcademique;
    public String mention;

    public Moyenneg() {}
    public Moyenneg(int id, int idEtudiant, double valeur, String semestre, String anneeAcademique, String mention) {
        this.id = id;
        this.idEtudiant = idEtudiant;
        this.valeur = valeur;
        this.semestre = semestre;
        this.anneeAcademique = anneeAcademique;
        this.mention = mention;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdEtudiant() {
        return idEtudiant;
    }
    public void setIdEtudiant(int idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    public double getValeur() {
        return valeur;
    }
    public void setValeur(double valeur) {
        this.valeur = valeur;
    }

    public String getSemestre() {
        return semestre;
    }
    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public String getAnneeAcademique() {
        return anneeAcademique;
    }
    public void setAnneeAcademique(String anneeAcademique) {
        this.anneeAcademique = anneeAcademique;
    }

    public String getMention() {
        return mention;
    }
    public void setMention(String mention) {
        this.mention = mention;
    }

    
}
