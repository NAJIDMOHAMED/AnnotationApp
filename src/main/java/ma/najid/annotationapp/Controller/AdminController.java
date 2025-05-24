package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.Dataset;
import ma.najid.annotationapp.Model.Tache;
import ma.najid.annotationapp.service.AnnotatorService;
import ma.najid.annotationapp.service.DatasetService;
import ma.najid.annotationapp.service.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AnnotatorService annotatorService;
    private final DatasetService datasetService;
    private final TacheService tacheService;

    @Autowired
    public AdminController(AnnotatorService annotatorService, DatasetService datasetService, TacheService tacheService) {
        this.annotatorService = annotatorService;
        this.datasetService = datasetService;
        this.tacheService = tacheService;
    }

//    @GetMapping("/tasks/assign/page")
//    public String showAssignTasksPage(Model model) {
//        System.out.println("\n=== DÉBUT DE L'AFFICHAGE DE LA PAGE D'ASSIGNATION ===");
//
//        // Récupérer le dataset actuel
//        Dataset currentDataset = datasetService.getDatasetById(getCurrentDatasetId());
//        System.out.println("Dataset actuel: " + (currentDataset != null ? currentDataset.getIdDataset() : "null"));
//
//        if (currentDataset == null) {
//            System.out.println("Aucun dataset trouvé");
//            model.addAttribute("errorMessage", "Veuillez d'abord créer un dataset.");
//            return "administrator/assign-tasks";
//        }
//
//        // Récupérer tous les annotateurs disponibles
//        List<Annotator> availableAnnotators = annotatorService.getAvailableAnnotators(currentDataset.getIdDataset());
//        System.out.println("Nombre d'annotateurs disponibles: " + availableAnnotators.size());
//
//        // Afficher les détails de chaque annotateur pour le débogage
//        availableAnnotators.forEach(annotator -> {
//            System.out.println("Annotateur disponible: " + annotator.getNom() + " " + annotator.getPrenom() +
//                    " (ID: " + annotator.getIdUser() + ")" +
//                    " - Rôle: " + (annotator.getRole() != null ? annotator.getRole().getNomRole() : "null"));
//        });
//
//        // Calculer le nombre de paires non assignées
//        int unassignedPairs = tacheService.getRemainingUnassignedPairs();
//        System.out.println("Nombre de paires non assignées: " + unassignedPairs);
//
//        int maxAnnotators = unassignedPairs / 20 + (unassignedPairs % 20 > 0 ? 1 : 0);
//        System.out.println("Nombre maximum d'annotateurs pouvant être assignés: " + maxAnnotators);
//
//        // Ajouter les attributs au modèle
//        model.addAttribute("dataset", currentDataset);
//        model.addAttribute("availableAnnotators", availableAnnotators);
//        model.addAttribute("unassignedPairs", unassignedPairs);
//        model.addAttribute("maxAnnotators", maxAnnotators);
//        model.addAttribute("pairsPerAnnotator", 20);
//
//        System.out.println("=== FIN DE L'AFFICHAGE DE LA PAGE D'ASSIGNATION ===\n");
//        return "administrator/assign-tasks";
//    }

    @PostMapping("/tasks/assign/ajax")
    @ResponseBody
    public ResponseEntity<?> assignTasks(@RequestBody List<Long> annotatorIds) {
        try {
            Dataset currentDataset = datasetService.getDatasetById(getCurrentDatasetId());
            if (currentDataset == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Aucun dataset trouvé"
                ));
            }

            int unassignedPairs = tacheService.getRemainingUnassignedPairs();
            if (annotatorIds.size() > unassignedPairs / 20) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Trop d'annotateurs sélectionnés. Il n'y a pas assez de paires de texte disponibles."
                ));
            }

            tacheService.assignTasksToAnnotators(currentDataset.getIdDataset(), annotatorIds);
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Tâches assignées avec succès"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Erreur lors de l'assignation des tâches: " + e.getMessage()
            ));
        }
    }


    @GetMapping("/tasks/list")
    public String listTasks(Model model) {
        List<Tache> taches = tacheService.getAllTasks();
        model.addAttribute("taches", taches);
        return "administrator/list-tasks";
    }


    private Long getCurrentDatasetId() {
        return 1L; // Pour l'instant, on utilise un ID fixe
    }
} 