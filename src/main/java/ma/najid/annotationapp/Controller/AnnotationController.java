package ma.najid.annotationapp.Controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ma.najid.annotationapp.Model.*;
import ma.najid.annotationapp.Model.TYPES.StatutTache;
import ma.najid.annotationapp.dto.AnnotationRequestDTO;
import ma.najid.annotationapp.dto.PossibleClassDTO;
import ma.najid.annotationapp.dto.AnnotationResponseDTO;
import ma.najid.annotationapp.repository.*;
import ma.najid.annotationapp.service.TacheService;
import ma.najid.annotationapp.service.PossibleClassesService;
import ma.najid.annotationapp.exception.AnnotationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.ArrayList;

@Controller
@RequestMapping("/annotate")
public class AnnotationController {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationController.class);
    private static final int MAX_PAIRS_PER_TASK = 20;

    private final TacheService tacheService;
    private final AnnotationRepository annotationRepository;
    private final PossibleClassesService possibleClassesService;
    private final TextPairRepository textPairRepository;
    private final DatasetRepository datasetRepository;
    private final AnnotatorRepository annotatorRepository;

    @Autowired
    public AnnotationController(
            TacheService tacheService,
            AnnotationRepository annotationRepository,
            PossibleClassesService possibleClassesService,
            TextPairRepository textPairRepository,
            DatasetRepository datasetRepository,
            AnnotatorRepository annotatorRepository) {
        this.tacheService = tacheService;
        this.annotationRepository = annotationRepository;
        this.possibleClassesService = possibleClassesService;
        this.textPairRepository = textPairRepository;
        this.datasetRepository = datasetRepository;
        this.annotatorRepository = annotatorRepository;
    }

    @GetMapping("/tache/{tacheId}")
    @Transactional(readOnly = true)
    public String annotateTache(
            @PathVariable Long tacheId,
            @RequestParam(defaultValue = "0") int index,
            HttpSession session,
            Model model) {
        try {
            // Validate session
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                logger.warn("No user session found");
                return "redirect:/login";
            }

            // Get annotator
            Annotator annotator = getAnnotator(userId);
            if (annotator == null) {
                return "redirect:/login";
            }

            // Get and validate task
            Tache tache = getAndValidateTask(tacheId, annotator);
            if (tache == null) {
                return "redirect:/annotator/home";
            }

            // Get all text pairs for this task
            List<TextPair> allPairs = new ArrayList<>(tache.getTextPairs());
            if (allPairs.isEmpty()) {
                logger.info("No text pairs found for task {}", tacheId);
                return "redirect:/annotator/home";
            }

            // Validate index
            if (index < 0 || index >= allPairs.size()) {
                index = 0;
            }

            // Get current text pair
            TextPair currentTextPair = allPairs.get(index);
            
            // Get annotations for current text pair
            List<Annotation> annotations = getAnnotationsForTextPairs(userId, List.of(currentTextPair));
            boolean isAnnotated = isTextPairAnnotated(annotations, currentTextPair);

            // Create response DTO
            AnnotationResponseDTO responseDTO = AnnotationResponseDTO.builder()
                .taskId(tache.getIdTache())
                .textPairId(currentTextPair.getIdTextPair())
                .text1(currentTextPair.getText1())
                .text2(currentTextPair.getText2())
                .currentIndex(index)
                .totalPairs(allPairs.size())
                .remainingPairs(allPairs.size() - (index + 1))
                .progress((index * 100.0) / allPairs.size())
                .isAnnotated(isAnnotated)
                .annotatorId(annotator.getIdUser())
                .build();

            // Convert possible classes to DTOs
            List<PossibleClassDTO> possibleClassDTOs = possibleClassesService.getAllPossibleClasses().stream()
                .map(pc -> {
                    PossibleClassDTO dto = new PossibleClassDTO();
                    dto.setId(pc.getIdPossibleClass());
                    dto.setTypeClass(pc.getTypeClass());
                    return dto;
                })
                .collect(Collectors.toList());

            // Add attributes to model
            model.addAttribute("tache", tache);
            model.addAttribute("response", responseDTO);
            model.addAttribute("possibleClasses", possibleClassDTOs);
            model.addAttribute("dataset", tache.getDataset());

            return "annotation/annotate";
        } catch (Exception e) {
            logger.error("Error in annotateTache: {}", e.getMessage(), e);
            return "redirect:/error";
        }
    }

    @PostMapping("/tache/{tacheId}")
    @Transactional
    public String saveAnnotation(
            @PathVariable Long tacheId,
            @Valid @ModelAttribute AnnotationRequestDTO requestDTO,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                bindingResult.getAllErrors().forEach(error -> 
                    redirectAttributes.addFlashAttribute("error", error.getDefaultMessage()));
                return "redirect:/annotate/tache/" + tacheId + "?index=" + requestDTO.getIndex();
            }

            // Validate session and authorization
            Long userId = validateSession(session);
            if (userId == null || !userId.equals(requestDTO.getAnnotatorId())) {
                logger.warn("Unauthorized annotation attempt: userId={}, annotatorId={}", 
                           userId, requestDTO.getAnnotatorId());
                return "redirect:/login";
            }

            // Get required entities
            Tache tache = tacheService.getTacheById(tacheId)
                    .orElseThrow(() -> new AnnotationException.ResourceNotFoundException("Task not found"));
            TextPair textPair = textPairRepository.findById(requestDTO.getTextPairId())
                    .orElseThrow(() -> new AnnotationException.ResourceNotFoundException("Text pair not found"));
            Annotator annotator = annotatorRepository.findById(requestDTO.getAnnotatorId())
                    .orElseThrow(() -> new AnnotationException.ResourceNotFoundException("Annotator not found"));
            PossibleClasses possibleClass = possibleClassesService.getPossibleClassById(requestDTO.getPossibleClassId())
                    .orElseThrow(() -> new AnnotationException.ResourceNotFoundException("Possible class not found"));

            // Check for duplicate annotation
            if (annotationRepository.existsByTextPairAndAnnotator(textPair, annotator)) {
                throw new AnnotationException.DuplicateAnnotationException(
                    "Annotation already exists for this text pair");
            }

            // Create and save annotation
            Annotation annotation = createAnnotation(textPair, annotator, possibleClass);
            annotationRepository.save(annotation);

            // Update task progress
            updateTaskProgress(tache);

            logger.info("Annotation saved successfully: taskId={}, textPairId={}, annotatorId={}", 
                       tacheId, requestDTO.getTextPairId(), requestDTO.getAnnotatorId());
            redirectAttributes.addFlashAttribute("success", "Annotation saved successfully");

            // Calculate next index
            List<TextPair> allPairs = new ArrayList<>(tache.getTextPairs());
            int nextIndex = requestDTO.getIndex() + 1;
            
            if (nextIndex >= allPairs.size()) {
                // All pairs annotated, redirect to home
                logger.info("All pairs annotated for task {}, redirecting to home", tacheId);
                return "redirect:/annotator/home?id=" + requestDTO.getAnnotatorId();
            }
            
            // Redirect to next pair
            logger.info("Redirecting to next pair: taskId={}, nextIndex={}", tacheId, nextIndex);
            return "redirect:/annotate/tache/" + tacheId + "?index=" + nextIndex;
        } catch (AnnotationException.ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (AnnotationException.DuplicateAnnotationException e) {
            logger.warn("Duplicate annotation attempt: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("Error saving annotation: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error saving annotation");
        }
        return "redirect:/annotate/tache/" + tacheId + "?index=" + requestDTO.getIndex();
    }

    // Helper methods
    private Long validateSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            logger.warn("No user session found");
            System.out.println( "No user session found" );
            return null;
        }
        return userId;
    }

    private Annotator getAnnotator(Long userId) {
        Optional<Annotator> annotatorOpt = annotatorRepository.findById(userId);
        if (annotatorOpt.isEmpty()) {
            logger.error("Annotator not found for userId {}", userId);
            return null;
        }
        return annotatorOpt.get();
    }

    private Tache getAndValidateTask(Long tacheId, Annotator annotator) {
        Optional<Tache> tacheOpt = tacheService.getTacheById(tacheId);
        if (tacheOpt.isEmpty()) {
            logger.error("Task not found with id {}", tacheId);
            return null;
        }
        Tache tache = tacheOpt.get();
        if (!tache.getAnnotator().getIdUser().equals(annotator.getIdUser())) {
            logger.warn("Unauthorized access attempt: annotator {} tried to access task {}", 
                       annotator.getIdUser(), tacheId);
            return null;
        }
        return tache;
    }

    private List<TextPair> getTextPairsWithPagination(Tache tache, int page, int size) {
        List<TextPair> allPairs = new ArrayList<>(tache.getTextPairs());
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, allPairs.size());
        
        if (startIndex >= allPairs.size()) {
            return Collections.emptyList();
        }
        
        return allPairs.subList(startIndex, endIndex);
    }

    private List<Annotation> getAnnotationsForTextPairs(Long userId, List<TextPair> textPairs) {
        List<Annotation> allAnnotations = annotationRepository.findByAnnotator_IdUser(userId);
        return allAnnotations.stream()
            .filter(a -> textPairs.contains(a.getTextPair()))
            .collect(Collectors.toList());
    }

    private boolean isTextPairAnnotated(List<Annotation> annotations, TextPair textPair) {
        return annotations.stream()
            .anyMatch(a -> a.getTextPair().getIdTextPair().equals(textPair.getIdTextPair()));
    }

    private void addModelAttributes(Model model, Tache tache, TextPair currentTextPair,
                                  List<TextPair> textPairs, int index, int size,
                                  List<Annotation> annotations, Annotator annotator,
                                  boolean isAnnotated) {
        model.addAttribute("tache", tache);
        model.addAttribute("textPair", currentTextPair);
        model.addAttribute("textPairs", textPairs);
        model.addAttribute("index", index);
        model.addAttribute("total", tache.getTextPairs().size());
        model.addAttribute("annotations", annotations);
        model.addAttribute("annotatorId", annotator.getIdUser());
        model.addAttribute("possibleClasses", possibleClassesService.getAllPossibleClasses());
        model.addAttribute("isAnnotated", isAnnotated);
        model.addAttribute("dataset", tache.getDataset());
        model.addAttribute("remainingPairs", tache.getTextPairs().size() - (index + 1));
    }

    private Annotation createAnnotation(TextPair textPair, Annotator annotator, PossibleClasses possibleClass) {
        Annotation annotation = new Annotation();
        annotation.setTextPair(textPair);
        annotation.setAnnotator(annotator);
        annotation.setPossibleClass(possibleClass);
        annotation.setDateAnnotation(new Date());
        return annotation;
    }

    private void updateTaskProgress(Tache tache) {
        // Récupérer toutes les paires de texte de la tâche
        Set<TextPair> textPairs = tache.getTextPairs();
        int totalPairs = textPairs.size();
        
        // Mettre à jour le statut d'annotation de chaque paire
        textPairs.forEach(TextPair::updateAnnotationStatus);
        
        // Compter le nombre de paires annotées
        int annotatedPairs = (int) textPairs.stream()
            .filter(TextPair::isAnnotated)
            .count();
        
        // Mettre à jour les valeurs
        tache.setNombreAnnotes(annotatedPairs);
        tache.setNombreTotal(totalPairs);
        
        // Mettre à jour le statut de la tâche
        if (annotatedPairs == 0) {
            tache.setStatut(StatutTache.NOT_STARTED);
        } else if (annotatedPairs < totalPairs) {
            tache.setStatut(StatutTache.IN_PROGRESS);
        } else {
            tache.setStatut(StatutTache.COMPLETED);
        }
        
        // Sauvegarder les modifications
        tacheService.saveTache(tache);
        
        // Log pour le débogage
        logger.info("Task progress updated - Task ID: {}, Annotated: {}, Total: {}, Status: {}", 
                   tache.getIdTache(), annotatedPairs, totalPairs, tache.getStatut());
    }

    public void createTestTache() {
        Dataset dataset = datasetRepository.findById(1L).orElseThrow();
        Tache tache = new Tache();
        tache.setDataset(dataset);
        tache.setDateDebut(new Date());
        tache.setDateLimite(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)); // 7 days from now
        Set<TextPair> textPairs = new HashSet<>(textPairRepository.findAll()); // or filter by dataset
        // Limiter à 20 paires
        if (textPairs.size() > MAX_PAIRS_PER_TASK) {
            textPairs = textPairs.stream()
                .limit(MAX_PAIRS_PER_TASK)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
        }
        tache.setTextPairs(textPairs);
        tacheService.saveTache(tache);
    }
} 