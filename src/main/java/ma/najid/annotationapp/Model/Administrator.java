package ma.najid.annotationapp.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@DiscriminatorValue("ADMINISTRATOR_ROLE")
@EqualsAndHashCode(callSuper = true)
public class Administrator extends UserAccount {
    // Additional administrator-specific fields can be added here
}
