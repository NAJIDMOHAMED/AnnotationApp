package ma.najid.annotationapp.Model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.najid.annotationapp.Model.TYPES.TYPECLASS;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PossibleClasses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPossibleClass;
    private TYPECLASS typeClass;

    @ManyToOne
    @JoinColumn(name = "dataset_id")
    private Dataset dataset;

}
