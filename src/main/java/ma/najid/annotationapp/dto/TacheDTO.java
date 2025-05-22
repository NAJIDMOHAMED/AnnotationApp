package ma.najid.annotationapp.dto;

import ma.najid.annotationapp.Model.Tache;

public  class TacheDTO {
    public Long idTache;
    public String annotateurNom;
    public String annotateurPrenom;
    public String annotateurEmail;
    public java.util.Date dateLimite;
    public int nombrePaires;

    public TacheDTO(Tache t) {
        this.idTache = t.getIdTache();
        this.annotateurNom = t.getAnnotator() != null ? t.getAnnotator().getNom() : null;
        this.annotateurPrenom = t.getAnnotator() != null ? t.getAnnotator().getPrenom() : null;
        this.annotateurEmail = t.getAnnotator() != null ? t.getAnnotator().getEmail() : null;
        this.dateLimite = t.getDateLimite();
        this.nombrePaires = t.getTextPairs() != null ? t.getTextPairs().size() : 0;
    }

}