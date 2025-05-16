package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.*;
import ma.najid.annotationapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@Controller
@RequestMapping("/annotate")
public class AnnotationController {
    private static final int ITEMS_PER_PAGE = 20;

    @Autowired
    private TacheRepository tacheRepository;
    @Autowired
    private AnnotationRepository annotationRepository;
    @Autowired
    private PossibleClassesRepository possibleClassesRepository;
    @Autowired
    private TextPairRepository textPairRepository;
    @Autowired
    private DatasetRepository datasetRepository;
    @Autowired
    private AnnotatorRepository annotatorRepository;

    // Affiche la page d'annotation pour un tache et un textPair donné (par défaut le premier non annoté)
    @GetMapping("/tache/{tacheId}")
    public String annotateTache(@PathVariable Long tacheId,
                                @RequestParam(value = "index", required = false, defaultValue = "0") int index,
                                @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                @RequestParam(value = "annotatorId", required = false) Long annotatorId,
                                Model model, java.security.Principal principal) {
        Optional<Tache> tacheOpt = tacheRepository.findById(tacheId);
        if (tacheOpt.isEmpty()) return "redirect:/error";
        
        Tache tache = tacheOpt.get();
        List<TextPair> allTextPairs = tache.getTextPairs().stream().toList();
        
        if (allTextPairs.isEmpty()) return "redirect:/error";
        
        // Calculer le nombre total de pages
        int totalPages = (int) Math.ceil((double) allTextPairs.size() / ITEMS_PER_PAGE);
        
        // Ajuster la page si nécessaire
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;
        
        // Calculer l'index de début et de fin pour la page courante
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allTextPairs.size());
        
        // Ajuster l'index si nécessaire
        if (index < startIndex) index = startIndex;
        if (index >= endIndex) index = endIndex - 1;
        
        TextPair currentPair = allTextPairs.get(index);
        List<PossibleClasses> possibleClasses = tache.getDataset().getPossibleClassesSet().stream().toList();
        
        if (annotatorId == null && principal != null) {
            String email = principal.getName();
            Annotator annotator = annotatorRepository.findByEmail(email).orElse(null);
            if (annotator != null) {
                annotatorId = annotator.getIdUser();
            }
        }
        if (annotatorId == null) {
            return "redirect:/login";
        }
        Annotator annotator = annotatorRepository.findById(annotatorId).orElse(null);
        if (annotator == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", annotator);
        model.addAttribute("annotatorId", annotatorId);
        
        model.addAttribute("tache", tache);
        model.addAttribute("dataset", tache.getDataset());
        model.addAttribute("textPair", currentPair);
        model.addAttribute("possibleClasses", possibleClasses);
        model.addAttribute("index", index);
        model.addAttribute("total", allTextPairs.size());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        return "annotation/annotate";
    }

    // Enregistre l'annotation et navigue
    @PostMapping("/tache/{tacheId}")
    public String saveAnnotation(@PathVariable Long tacheId,
                                 @RequestParam Long textPairId,
                                 @RequestParam Long possibleClassId,
                                 @RequestParam int index,
                                 @RequestParam int page,
                                 @RequestParam String action,
                                 @RequestParam Long annotatorId,
                                 RedirectAttributes redirectAttributes) {
        // Récupérer l'annotateur par son id
        Annotator annotator = annotatorRepository.findById(annotatorId).orElse(null);
        // Enregistrer l'annotation
        Annotation annotation = new Annotation();
        annotation.setAnnotator(annotator);
        annotation.setTextPair(textPairRepository.findById(textPairId).orElse(null));
        annotation.setPossibleClass(possibleClassesRepository.findById(possibleClassId).orElse(null));
        annotation.setDateAnnotation(new java.util.Date());
        annotationRepository.save(annotation);
        // Navigation
        int nextIndex = index;
        int nextPage = page;
        if ("next".equals(action)) {
            nextIndex++;
            if (nextIndex % ITEMS_PER_PAGE == 0) {
                nextPage++;
            }
        } else if ("prev".equals(action)) {
            nextIndex--;
            if (nextIndex < 0) {
                nextPage--;
                nextIndex = ITEMS_PER_PAGE - 1;
            }
        } else if ("page".equals(action)) {
            nextIndex = page * ITEMS_PER_PAGE;
        }
        redirectAttributes.addAttribute("index", nextIndex);
        redirectAttributes.addAttribute("page", nextPage);
        redirectAttributes.addAttribute("annotatorId", annotatorId);
        return "redirect:/annotate/tache/" + tacheId;
    }

    public void createTestTache() {
        Dataset dataset = datasetRepository.findById(1L).orElseThrow();
        Tache tache = new Tache();
        tache.setDataset(dataset);
        tache.setDateLimite(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)); // 7 days from now
        Set<TextPair> textPairs = new HashSet<>(textPairRepository.findAll()); // or filter by dataset
        tache.setTextPairs(textPairs);
        tacheRepository.save(tache);
    }

} 