package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.UserAccount;
import ma.najid.annotationapp.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/annotator/profile")
public class AnnotatorProfileController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping
    public String showProfile(@RequestParam Long id, Model model) {
        Optional<UserAccount> userOpt = userAccountService.getUserAccountById(id);
        
        if (userOpt.isPresent()) {
            UserAccount user = userOpt.get();
            model.addAttribute("user", user);
            model.addAttribute("username", user.getNom() + " " + user.getPrenom());
            return "annotator/profile";
        }
        return "redirect:/login";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam Long id, 
                              @ModelAttribute UserAccount updatedUser, 
                              RedirectAttributes redirectAttributes) {
        try {
            Optional<UserAccount> userOpt = userAccountService.getUserAccountById(id);
            
            if (userOpt.isPresent()) {
                UserAccount currentUser = userOpt.get();
                // Update only allowed fields
                currentUser.setNom(updatedUser.getNom());
                currentUser.setPrenom(updatedUser.getPrenom());
                currentUser.setEmail(updatedUser.getEmail());

                userAccountService.saveUserAccount(currentUser);
                redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
        }
        return "redirect:/annotator/profile?id=" + id;
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam Long id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<UserAccount> userOpt = userAccountService.getUserAccountById(id);
            
            if (userOpt.isPresent()) {
                UserAccount user = userOpt.get();
                // Verify current password
                if (!currentPassword.equals(user.getPassword())) {
                    redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
                    return "redirect:/annotator/profile?id=" + id;
                }

                // Verify new passwords match
                if (!newPassword.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("error", "New passwords do not match");
                    return "redirect:/annotator/profile?id=" + id;
                }

                // Update password
                user.setPassword(newPassword);
                userAccountService.saveUserAccount(user);
                redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to change password: " + e.getMessage());
        }
        return "redirect:/annotator/profile?id=" + id;
    }

    @GetMapping("/delete")
    public String deleteAccount(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<UserAccount> userOpt = userAccountService.getUserAccountById(id);
            
            if (userOpt.isPresent()) {
                UserAccount user = userOpt.get();
                userAccountService.deleteUserAccount(user.getIdUser());
                redirectAttributes.addFlashAttribute("success", "Account deleted successfully");
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete account: " + e.getMessage());
        }
        return "redirect:/annotator/profile?id=" + id;
    }
} 