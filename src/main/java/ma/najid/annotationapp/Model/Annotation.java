package ma.najid.annotationapp.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Annotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAnnotation;

    @ManyToOne
    @JoinColumn(name = "id_possible_class")
    private PossibleClasses possibleClasses;

    @ManyToOne
    @JoinColumn(name = "annotator_id")
    private Annotator annotator;

    @ManyToOne
    @JoinColumn(name = "id_text_pair") // ✅ correspond au nom utilisé dans TextPair
    private TextPair textPair;
}
