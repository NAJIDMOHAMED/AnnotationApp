package ma.najid.annotationapp.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(exclude = {"annotator", "textPair", "possibleClass"})
public class Annotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAnnotation;

    @ManyToOne
    @JoinColumn(
        name = "id_annotator",
        foreignKey = @ForeignKey(name = "FK_ANNOTATION_ANNOTATOR"),
        referencedColumnName = "idUser"
    )
    private Annotator annotator;

    @ManyToOne
    @JoinColumn(name = "id_text_pair")
    private TextPair textPair;

    @ManyToOne
    @JoinColumn(name = "id_possible_class")
    private PossibleClasses possibleClass;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAnnotation;


}
