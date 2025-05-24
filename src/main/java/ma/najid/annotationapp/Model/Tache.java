package ma.najid.annotationapp.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import  ma.najid.annotationapp.Model.TYPES.StatutTache;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTache;

    @Column(name = "nom_tache")
    private String nomTache;

    @Column(name = "date_debut")
    private Date dateDebut;

    @Column(name = "date_limite")
    private Date dateLimite;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutTache statut = StatutTache.NOT_STARTED;

    @Column(name = "nombre_annotes")
    private Integer nombreAnnotes = 0;

    @Column(name = "nombre_total")
    private Integer nombreTotal = 0;

    @ManyToOne
    @JoinColumn(name = "idUser") // OK car idUser est dans UserAccount, et Annotator hérite de UserAccount
    private Annotator annotator;


    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TextPair> textPairs = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "id_dataset")
    private Dataset dataset;



    public double getPourcentageComplet() {
        if (nombreTotal == 0) return 0;
        return (double) nombreAnnotes / nombreTotal * 100;
    }

    public String getTempsEstime() {
        if (nombreAnnotes == 0 || nombreTotal == 0) return "Non estimé";
        if (dateDebut == null || dateLimite == null) return "Dates non définies";
        
        long tempsTotal = dateLimite.getTime() - dateDebut.getTime();
        long tempsRestant = tempsTotal - (System.currentTimeMillis() - dateDebut.getTime());
        
        if (tempsRestant <= 0) return "Délai dépassé";
        
        long jours = tempsRestant / (1000 * 60 * 60 * 24);
        long heures = (tempsRestant % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        
        return jours + "j " + heures + "h";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tache tache = (Tache) o;
        return Objects.equals(idTache, tache.idTache);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTache);
    }
}
