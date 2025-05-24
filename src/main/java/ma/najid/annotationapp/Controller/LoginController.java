package ma.najid.annotationapp.Controller;

import jakarta.servlet.http.HttpSession;
import ma.najid.annotationapp.Model.UserAccount;
import ma.najid.annotationapp.Model.TYPES.TypeRole;
import ma.najid.annotationapp.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

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
                       HttpSession session,
                       Model model) {
        logger.info("Attempting login for email: {}", email);
        
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            logger.warn("Login attempt with empty email or password");
            model.addAttribute("error", "Email and password are required");
            return "LoginForm";
        }

        try {
            Optional<UserAccount> userOpt = userAccountService.getUserAccountByEmail(email);
            if (userOpt.isPresent()) {
                UserAccount user = userOpt.get();
                logger.debug("User found: {}", user.getEmail());
                
                if (password.equals(user.getPassword())) {
                    logger.info("Login successful for user: {}", user.getEmail());
                    
                    // Stocker les informations de l'utilisateur dans la session
                    session.setAttribute("userId", user.getIdUser());
                    session.setAttribute("userEmail", user.getEmail());
                    session.setAttribute("userRole", user.getRole().getNomRole());

                    // Rediriger selon le rôle
                    if (user.getRole().getNomRole() == TypeRole.ADMIN_ROLE) {
                        return "redirect:/admin/adminHome?id=" + user.getIdUser();
                    } else if (user.getRole().getNomRole() == TypeRole.ANNOTATOR_ROLE) {
                        return "redirect:/annotator/home?id=" + user.getIdUser();
                    } else {
                        logger.warn("Unknown role for user: {}", user.getEmail());
                        model.addAttribute("error", "Invalid user role");
                    }
                } else {
                    logger.warn("Invalid password for user: {}", email);
                    model.addAttribute("error", "Invalid email or password");
                }
            } else {
                logger.warn("No user found with email: {}", email);
                model.addAttribute("error", "Invalid email or password");
            }
        } catch (Exception e) {
            logger.error("Error during login for email: {}", email, e);
            model.addAttribute("error", "An error occurred during login");
        }
        return "LoginForm";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        logger.info("Logging out user: {}", userEmail);
        session.invalidate();
        return "redirect:/login";
    }
}
