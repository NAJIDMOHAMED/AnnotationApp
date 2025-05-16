package ma.najid.annotationapp.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dataset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDataset;
    private String nom;
    private String description;

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PossibleClasses> possibleClassesSet;

    @OneToMany(mappedBy ="dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Tache> taches;

    @OneToMany(mappedBy ="dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TextPair> textPairs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dataset dataset = (Dataset) o;
        return Objects.equals(idDataset, dataset.idDataset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDataset);
    }
}
