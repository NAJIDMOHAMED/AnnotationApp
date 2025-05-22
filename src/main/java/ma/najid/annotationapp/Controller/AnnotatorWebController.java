package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.service.AnnotatorService;
import ma.najid.annotationapp.service.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/annotator")
public class AnnotatorWebController {
    private final AnnotatorService annotatorService;
    private final TacheService tacheService;

    @Autowired
    public AnnotatorWebController(AnnotatorService annotatorService, TacheService tacheService) {
        this.annotatorService = annotatorService;
        this.tacheService = tacheService;
    }

    @GetMapping("/list")
    public String listAnnotators(Model model) {
        List<Annotator> annotators = annotatorService.getAllAnnotators();
        model.addAttribute("annotators", annotators);
        return "annotator/list";
    }

    @GetMapping("/tache/{tacheId}/details")
    public String showTaskDetails(@PathVariable Long tacheId, 
                                HttpSession session,
                                Model model) {
        // Vérifier si l'utilisateur est connecté
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // Récupérer la tâche
        var tacheOpt = tacheService.getTacheById(tacheId);
        if (tacheOpt.isEmpty()) {
            return "redirect:/annotator/home";
        }

        var tache = tacheOpt.get();

        // Vérifier si l'annotateur est assigné à cette tâche
        if (!tache.getAnnotator().getIdUser().equals(userId)) {
            return "redirect:/error";
        }

        // Calculer les statistiques
        int totalPairs = tache.getTextPairs().size();
        long annotatedPairs = tache.getTextPairs().stream()
            .filter(pair -> !pair.getAnnotations().isEmpty())
            .count();
        double progress = totalPairs > 0 ? (double) annotatedPairs / totalPairs * 100 : 0;

        model.addAttribute("tache", tache);
        model.addAttribute("totalPairs", totalPairs);
        model.addAttribute("annotatedPairs", annotatedPairs);
        model.addAttribute("progress", progress);
        model.addAttribute("annotatorId", userId);

        return "annotator/task-details";
    }
} 