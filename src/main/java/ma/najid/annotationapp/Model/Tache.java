package ma.najid.annotationapp.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    @Column(name = "date_limite")
    private Date dateLimite;

    @ManyToOne
    @JoinColumn(name = "annotator_id")
    private Annotator annotator;

    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TextPair> textPairs = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "id_dataset")
    private Dataset dataset;

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
