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
public class TextPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_text_pair") // ✅ Nom explicite et uniforme
    private Long idTextPair;

    @Column(length = 2550)
    private String text1;

    @Column(length = 2550)
    private String text2;

    @OneToMany(mappedBy = "textPair", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Annotation> annotations;

    @ManyToOne
    @JoinColumn(name = "tache_id")
    private Tache tache;

    @ManyToOne
    @JoinColumn(name = "id_dataset")
    private Dataset dataset;

    @Column(name = "is_annotated")
    private boolean isAnnotated = false;

    public boolean isAnnotated() {
        return annotations != null && !annotations.isEmpty();
    }

    public void updateAnnotationStatus() {
        this.isAnnotated = isAnnotated();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextPair textPair = (TextPair) o;
        return Objects.equals(idTextPair, textPair.idTextPair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTextPair);
    }
}
