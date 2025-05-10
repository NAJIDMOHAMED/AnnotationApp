package ma.najid.annotationapp.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Entity
@Data
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTache;

    @Column(name = "date_limite")
    private Date dateLimite;

    @ManyToOne
    @JoinColumn(name = "annotator_id")
    private Annotator annotator;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "TextPair_Taches",
            joinColumns = @JoinColumn(name = "id_tache"),
            inverseJoinColumns = @JoinColumn(name = "id_text_pair") // ✅ cohérent avec TextPair
    )
    private Set<TextPair> textPairSet;

    @ManyToOne
    @JoinColumn(name = "id_dataset")
    private Dataset dataset;
}
