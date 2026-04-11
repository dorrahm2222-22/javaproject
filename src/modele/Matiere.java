package modele;
public class Matiere {
    private int id;
    private String nom;
    private int coefficient;
    private int volumeHoraire;
    private String semestre;

    public Matiere() {}
    public Matiere(int id, String nom, int coefficient, int volumeHoraire, String semestre) {
        this.id = id;
        this.nom = nom;
        this.coefficient = coefficient;
        this.volumeHoraire = volumeHoraire;
        this.semestre = semestre;
    }

    public int getId() {
        return id;
    }
     public void setId(int id) {
        this.id = id;
    }
    

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }


    public int getCoefficient() {
        return coefficient;
    }
     public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }


    public int getVolumeHoraire() {
        return volumeHoraire;
    }
    public void setVolumeHoraire(int volumeHoraire) {
        this.volumeHoraire = volumeHoraire;
    }


    public String getSemestre() {
        return semestre;
    }
    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }


    
}
