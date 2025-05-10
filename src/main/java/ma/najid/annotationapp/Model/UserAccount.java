package ma.najid.annotationapp.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
public abstract class UserAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idUser;

	@NotBlank(message = "Last name is required")
	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
	@Column(nullable = false)
	private String nom;

	@NotBlank(message = "First name is required")
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	@Column(nullable = false)
	private String prenom;

	@NotBlank(message = "Email is required")
	@Email(message = "Please provide a valid email address")
	@Column(nullable = false, unique = true)
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 4, message = "Password must be at least 6 characters long")
	@Pattern(regexp = "^(?=.*[0-9])$",
			 message = "Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character")
	@Column(nullable = false)///(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{6,}
	private String password;

	@NotNull(message = "Role is required")
	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

}