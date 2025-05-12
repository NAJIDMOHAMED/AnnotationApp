package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.UserAccount;
import ma.najid.annotationapp.Model.TYPES.TypeRole;
import ma.najid.annotationapp.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.util.StringUtils;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "LoginForm";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, 
                       @RequestParam String password, 
                       Model model) {
        // Validate input
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            model.addAttribute("error", "Email and password are required");
            return "LoginForm";
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            model.addAttribute("error", "Invalid email format");
            return "LoginForm";
        }

        try {
            Optional<UserAccount> userOpt = userAccountService.getUserAccountByEmail(email);
            
            if (userOpt.isPresent() && password.equals(userOpt.get().getPassword())) {
                UserAccount user = userOpt.get();
                model.addAttribute("username", user.getEmail());
                
                // Check user role and redirect accordingly
                if (user.getRole().getNomRole() == TypeRole.ADMIN_ROLE) {
                    return "redirect:/admin/adminHome?id=" + user.getIdUser();
                } else if (user.getRole().getNomRole() == TypeRole.ANNOTATOR_ROLE) {
                    return "redirect:/annotator/home?id=" + user.getIdUser();
                } else {
                    model.addAttribute("error", "Invalid user role");
                    return "LoginForm";
                }
            } else {
                model.addAttribute("error", "Invalid email or password");
                return "LoginForm";
            }
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during login");
            return "LoginForm";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
}
