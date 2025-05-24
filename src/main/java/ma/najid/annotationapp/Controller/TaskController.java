package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.Tache;
import ma.najid.annotationapp.service.AnnotatorService;
import ma.najid.annotationapp.service.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/tasks")
public class TaskController {

    @Autowired
    private TacheService tacheService;

    @Autowired
    private AnnotatorService annotatorService;

    @GetMapping("/assign")
    public String showAssignTasksPage(Model model) {
        // Récupérer tous les annotateurs
        List<Annotator> allAnnotators = annotatorService.getAllAnnotators();
        System.out.println("Nombre total d'annotateurs: " + allAnnotators.size());

        // Afficher les détails de chaque annotateur pour le débogage
        allAnnotators.forEach(annotator -> {
            System.out.println("Annotateur: " + annotator.getNom() + " " + annotator.getPrenom() +
                    " (ID: " + annotator.getIdUser() + ")");
        });

        int unassignedPairs = tacheService.getRemainingUnassignedPairs();
        int maxAnnotators = unassignedPairs / 20 + (unassignedPairs % 20 > 0 ? 1 : 0);

        // Utiliser directement la liste complète des annotateurs
        model.addAttribute("annotators", allAnnotators);
        model.addAttribute("unassignedPairs", unassignedPairs);
        model.addAttribute("maxAnnotators", maxAnnotators);
        model.addAttribute("pairsPerAnnotator", 20);

        return "administrator/assign-tasks";
    }

    @PostMapping("/assign")
    @ResponseBody
    public ResponseEntity<?> assignTasks(@RequestBody List<Long> annotatorIds) {
        try {
            if (annotatorIds.size() > tacheService.getRemainingUnassignedPairs() / 20) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Trop d'annotateurs sélectionnés. Il n'y a pas assez de paires de texte disponibles."
                ));
            }

            List<Tache> createdTasks = tacheService.assignTasksToAnnotators(annotatorIds);
            // Création des DTOs pour éviter les problèmes de proxy Hibernate
            List<TacheDTO> dtos = createdTasks.stream().map(TacheDTO::new).toList();
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Tâches assignées avec succès",
                    "tasks", dtos
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // DTO interne pour la réponse JSON
    public static class TacheDTO {
        public Long idTache;
        public String annotateurNom;
        public String annotateurPrenom;
        public String annotateurEmail;
        public java.util.Date dateLimite;
        public int nombrePaires;

        public TacheDTO(Tache t) {
            this.idTache = t.getIdTache();
            this.annotateurNom = t.getAnnotator() != null ? t.getAnnotator().getNom() : null;
            this.annotateurPrenom = t.getAnnotator() != null ? t.getAnnotator().getPrenom() : null;
            this.annotateurEmail = t.getAnnotator() != null ? t.getAnnotator().getEmail() : null;
            this.dateLimite = t.getDateLimite();
            this.nombrePaires = t.getTextPairs() != null ? t.getTextPairs().size() : 0;
        }
    }
//
//    @GetMapping("/list")
//    public String listTasks(Model model) {
//        List<Tache> taches = tacheService.getAllTasks();
//        model.addAttribute("taches", taches);
//        return "administrator/list-tasks";
//    }
} 