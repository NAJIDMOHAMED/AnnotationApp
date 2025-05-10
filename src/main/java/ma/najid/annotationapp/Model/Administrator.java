package ma.najid.annotationapp.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
//@DiscriminatorValue("ADMIN_ROLE")
public class Administrator extends UserAccount {
    // No need for additional ID field as it inherits from Utilisateur
}
