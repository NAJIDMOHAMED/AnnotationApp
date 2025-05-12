package ma.najid.annotationapp.Model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PossibleClasses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPossibleClass;
    private String typeClass;

    @ManyToOne
    @JoinColumn(name = "dataset_id")
    private Dataset dataset;

}
