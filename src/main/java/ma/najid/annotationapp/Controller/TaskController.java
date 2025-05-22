package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.Dataset;
import ma.najid.annotationapp.Model.Tache;
import ma.najid.annotationapp.dto.TacheDTO;
import ma.najid.annotationapp.service.AnnotatorService;
import ma.najid.annotationapp.service.TacheService;
import ma.najid.annotationapp.service.DatasetService;
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

    @Autowired
    private DatasetService datasetService;

    @GetMapping("/assign/page")
    public String showAssignTasksPage(Model model) {
        Dataset currentDataset = datasetService.getDatasetById(1L); // TODO: Remplacer par getCurrentDatasetId()
        if (currentDataset == null) {
            model.addAttribute("errorMessage", "Veuillez d'abord créer un dataset.");
            return "administrator/assign-tasks";
        }

        // Récupérer les annotateurs assignés
        List<Annotator> assignedAnnotators = annotatorService.getAnnotatorsByDataset(currentDataset.getIdDataset());
        
        // Récupérer tous les annotateurs
        List<Annotator> allAnnotators = annotatorService.getAllAnnotators();
        
        // Filtrer pour obtenir les annotateurs disponibles
        List<Annotator> availableAnnotators = allAnnotators.stream()
            .filter(annotator -> !assignedAnnotators.contains(annotator))
            .toList();

        int unassignedPairs = tacheService.getRemainingUnassignedPairs();
        int maxAnnotators = unassignedPairs / 20 + (unassignedPairs % 20 > 0 ? 1 : 0);
        
        model.addAttribute("dataset", currentDataset);
        model.addAttribute("availableAnnotators", availableAnnotators);
        model.addAttribute("assignedAnnotators", assignedAnnotators);
        model.addAttribute("unassignedPairs", unassignedPairs);
        model.addAttribute("maxAnnotators", maxAnnotators);
        model.addAttribute("pairsPerAnnotator", 20);
        return "administrator/assign-tasks";
    }

    @PostMapping("/assign/ajax")
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

    @GetMapping("/list")
    public String listTasks(Model model) {
        List<Tache> taches = tacheService.getAllTasks();
        model.addAttribute("taches", taches);
        return "administrator/list-tasks";
    }
} 