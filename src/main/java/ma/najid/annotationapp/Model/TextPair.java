package ma.najid.annotationapp.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_text_pair") // ✅ Nom explicite et uniforme
    private Long idTextPair;

    @Column
    private String text1;

    @Column
    private String text2;

    @OneToMany(mappedBy = "textPair", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Annotation> annotations;

    @ManyToMany(mappedBy = "textPairSet", cascade = CascadeType.ALL)
    private Set<Tache> taches;

    @ManyToOne
    @JoinColumn(name = "id_dataset")
    private Dataset dataset;
}
