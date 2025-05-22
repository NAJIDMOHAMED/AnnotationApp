package ma.najid.annotationapp.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginRequest {

    @NotBlank(message = "ID is required")
    private Long id; // ou `Long id` si vous utilisez un champ numérique

    @NotBlank(message = "Password is required")
    private String password;

    // Getters & Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
