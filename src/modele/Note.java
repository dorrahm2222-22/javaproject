package modele;

import java.util.Date;

public class Note {
    private int id;
    private int idEtudiant;
    private int idMatiere;
    private double note;
    private String typeEvaluation;
    private Date date_evaluation;
    
    public Note() {}
    public Note(int id, int idEtudiant, int idMatiere, double note, String typeEvaluation, Date date_evaluation) {
        this.id = id;
        this.idEtudiant = idEtudiant;
        this.idMatiere = idMatiere;
        this.note = note;
        this.typeEvaluation = typeEvaluation;
        this.date_evaluation = date_evaluation;
    }

    public int getIdMatiere() {
        return idMatiere;
    }
    public void setIdMatiere(int idMatiere) {
        this.idMatiere = idMatiere;
    }


    public double getNote() {
        return note;
    }
    public void setNote(double note) {
        this.note = note;
    }


    public String getTypeEvaluation() {
        return typeEvaluation;
    }
    public void setTypeEvaluation(String typeEvaluation) {
        this.typeEvaluation = typeEvaluation;
    }


    public Date getDate_evaluation() {
        return date_evaluation;
    }
    public void setDate_evaluation(Date date_evaluation) {
        this.date_evaluation = date_evaluation;
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
}
