package ma.najid.annotationapp.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;
import lombok.EqualsAndHashCode;



@Entity
@Data
@DiscriminatorValue("ANNOTATOR_ROLE")
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "idUser")
public class Annotator extends UserAccount {

    @OneToMany(mappedBy = "annotator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Tache> taches;


    @OneToMany(mappedBy = "annotator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Annotation> annotations;



}
