package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.UserAccount;
import ma.najid.annotationapp.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/annotator")
public class AnnotatorHomeController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/home")
    public String showHome(@RequestParam Long id, Model model) {
        Optional<UserAccount> userOpt = userAccountService.getUserAccountById(id);
        
        if (userOpt.isPresent()) {
            UserAccount user = userOpt.get();
            model.addAttribute("user", user);
            model.addAttribute("username", user.getNom() + " " + user.getPrenom());
            return "annotator/AnnotatorHome";
        }
        return "redirect:/login";
    }
} 