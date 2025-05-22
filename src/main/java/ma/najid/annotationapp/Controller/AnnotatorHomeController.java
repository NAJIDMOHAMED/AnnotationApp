package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.UserAccount;
import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.Tache;
import ma.najid.annotationapp.Model.TYPES.StatutTache;
import ma.najid.annotationapp.service.AnnotatorService;
import ma.najid.annotationapp.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/annotator")
public class AnnotatorHomeController {

    private static final Logger logger = LoggerFactory.getLogger(AnnotatorHomeController.class);

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AnnotatorService annotatorService;

    @GetMapping("/home")
    public String showAnnotatorHome(@RequestParam(required = false) Long id, Model model) {
        Optional<UserAccount> userOpt = Optional.empty();

        try {
            if (id != null) {
                userOpt = userAccountService.getUserAccountById(id);
            }

            if (userOpt.isEmpty()) {
                logger.warn("Aucun utilisateur trouvé pour id={} ou principal={}", id,  "null");
                return "redirect:/login";
            }

            UserAccount user = userOpt.get();
            model.addAttribute("user", user);
            model.addAttribute("username", user.getNom() + " " + user.getPrenom());

            if (user instanceof Annotator annotator) {
                annotatorService.prepareAnnotatorHomeData(annotator, model);
            }

        } catch (Exception e) {
            logger.error("Erreur dans showAnnotatorHome : {}", e.getMessage(), e);
            return "redirect:/login";
        }

        return "annotator/AnnotatorHome";
    }
}
