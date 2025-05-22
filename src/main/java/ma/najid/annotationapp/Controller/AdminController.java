package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.Dataset;
import ma.najid.annotationapp.Model.Tache;
import ma.najid.annotationapp.service.AnnotatorService;
import ma.najid.annotationapp.service.DatasetService;
import ma.najid.annotationapp.service.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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

    @GetMapping("/tasks/assign")
    public String showAssignTasksPage(Model model) {
        Dataset currentDataset = datasetService.getDatasetById(getCurrentDatasetId());
        System.out.println("Dataset actuel: " + (currentDataset != null ? currentDataset.getIdDataset() : "null"));
        
        if (currentDataset == null) {
            model.addAttribute("errorMessage", "Veuillez d'abord créer un dataset.");
            return "administrator/assign-tasks";
        }

        // Récupérer les annotateurs assignés
        List<Annotator> assignedAnnotators = annotatorService.getAnnotatorsByDataset(currentDataset.getIdDataset());
        System.out.println("Nombre d'annotateurs assignés dans le contrôleur: " + assignedAnnotators.size());
        
        // Récupérer tous les annotateurs
        List<Annotator> allAnnotators = annotatorService.getAllAnnotators();
        System.out.println("Nombre total d'annotateurs: " + allAnnotators.size());
        
        // Filtrer pour obtenir les annotateurs disponibles (ceux qui ne sont pas assignés)
        List<Annotator> availableAnnotators = allAnnotators.stream()
            .filter(annotator -> !assignedAnnotators.contains(annotator))
            .toList();
        
        System.out.println("Nombre d'annotateurs disponibles: " + availableAnnotators.size());

        model.addAttribute("dataset", currentDataset);
        model.addAttribute("availableAnnotators", availableAnnotators);
        model.addAttribute("assignedAnnotators", assignedAnnotators);
        return "administrator/assign-tasks";
    }

    @PostMapping("/tasks/assign")
    public String assignTasks(@RequestParam("annotatorIds") List<Long> annotatorIds,
                            @RequestParam("datasetId") Long datasetId,
                            RedirectAttributes redirectAttributes) {
        try {
            tacheService.assignTasksToAnnotators(datasetId, annotatorIds);
            redirectAttributes.addFlashAttribute("successMessage", "Tâches assignées avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'assignation des tâches: " + e.getMessage());
        }
        return "redirect:/admin/tasks/assign";
    }

    @PostMapping("/tasks/remove-annotator")
    public String removeAnnotator(@RequestParam("annotatorId") Long annotatorId,
                                @RequestParam("datasetId") Long datasetId,
                                RedirectAttributes redirectAttributes) {
        try {
            tacheService.removeAnnotatorFromDataset(annotatorId, datasetId);
            redirectAttributes.addFlashAttribute("successMessage", "Annotateur retiré avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors du retrait de l'annotateur: " + e.getMessage());
        }
        return "redirect:/admin/tasks/assign";
    }

    private Long getCurrentDatasetId() {
        // Pour l'instant, on retourne un ID fixe
        // TODO: Implémenter la logique pour obtenir l'ID du dataset actuel
        return 1L;
    }
} 