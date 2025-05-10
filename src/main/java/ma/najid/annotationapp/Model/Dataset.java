package ma.najid.annotationapp.Model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
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
}
