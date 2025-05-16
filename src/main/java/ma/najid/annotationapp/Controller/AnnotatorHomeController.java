package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.UserAccount;
import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.Tache;
import ma.najid.annotationapp.repository.TacheRepository;
import ma.najid.annotationapp.repository.AnnotatorRepository;
import ma.najid.annotationapp.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/annotator")
public class AnnotatorHomeController {

    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private TacheRepository tacheRepository;
    @Autowired
    private AnnotatorRepository annotatorRepository;

    @GetMapping("/home")
    public String showHome(@RequestParam Long id, Model model) {
        Optional<UserAccount> userOpt = userAccountService.getUserAccountById(id);
        if (userOpt.isPresent()) {
            UserAccount user = userOpt.get();
            model.addAttribute("user", user);
            model.addAttribute("username", user.getNom() + " " + user.getPrenom());
            if (user instanceof Annotator) {
                List<Tache> taches = tacheRepository.findByAnnotator_IdUser(user.getIdUser());
                model.addAttribute("taches", taches);
                if (taches.isEmpty()) {
                    model.addAttribute("noTaskMessage", "Vous n'avez aucune tâche à annoter pour le moment.");
                }
            }
            return "annotator/AnnotatorHome";
        }
        return "redirect:/login";
    }
} 