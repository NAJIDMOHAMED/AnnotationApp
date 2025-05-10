package ma.najid.annotationapp.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.najid.annotationapp.Model.TYPES.TypeRole;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idRole;
	@Enumerated(EnumType.STRING)
	private TypeRole nomRole;
}