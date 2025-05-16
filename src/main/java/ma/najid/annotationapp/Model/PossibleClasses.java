package ma.najid.annotationapp.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PossibleClasses that = (PossibleClasses) o;
        return Objects.equals(idPossibleClass, that.idPossibleClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPossibleClass);
    }
}
