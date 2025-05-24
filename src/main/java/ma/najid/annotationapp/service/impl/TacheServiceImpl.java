package ma.najid.annotationapp.service.impl;

import ma.najid.annotationapp.Model.Tache;
import ma.najid.annotationapp.Model.TYPES.StatutTache;
import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.TextPair;
import ma.najid.annotationapp.Model.Dataset;
import ma.najid.annotationapp.Model.TYPES.StatutTache;
import ma.najid.annotationapp.repository.TacheRepository;
import ma.najid.annotationapp.repository.AnnotatorRepository;
import ma.najid.annotationapp.repository.TextPairRepository;
import ma.najid.annotationapp.service.TacheService;
import ma.najid.annotationapp.service.DatasetService;
import ma.najid.annotationapp.service.AnnotatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Service
@Transactional
@PropertySource("classpath:application.properties")
public class TacheServiceImpl implements TacheService {

    private static final Logger log = LoggerFactory.getLogger(TacheServiceImpl.class);
    private final AnnotatorService annotatorService;

    @Value("${annotation.pairs.per.annotator}")
    private int pairsPerAnnotator;

    private final TacheRepository tacheRepository;
    private final AnnotatorRepository annotatorRepository;
    private final TextPairRepository textPairRepository;
    private final DatasetService datasetService;

    @Autowired
    public TacheServiceImpl(TacheRepository tacheRepository,
                            AnnotatorRepository annotatorRepository,
                            TextPairRepository textPairRepository,
                            DatasetService datasetService,
                            AnnotatorService annotatorService) {
        this.tacheRepository = tacheRepository;
        this.annotatorRepository = annotatorRepository;
        this.textPairRepository = textPairRepository;
        this.datasetService = datasetService;
        this.annotatorService = annotatorService;
    }

    @Override
    public Tache saveTache(Tache tache) {
        return tacheRepository.save(tache);
    }


    @Override
    public List<Tache> getAllTasks() {
        return tacheRepository.findAll();
    }

    @Override
    public Optional<Tache> getTacheById(Long id) {
        return tacheRepository.findById(id);
    }

    @Override
    public void deleteTache(Long id) {
        tacheRepository.deleteById(id);
    }

    @Override
    public List<Tache> getTachesByAnnotator(Annotator annotator) {
        return tacheRepository.findByAnnotator(annotator);
    }

    @Override
    public List<Tache> getTachesByAnnotatorId(Long annotatorId) {
        return tacheRepository.findByAnnotator_IdUser(annotatorId);
    }

    @Override
    public List<Tache> getTachesByDatasetId(Long datasetId) {
        return tacheRepository.findByDataset_IdDataset(datasetId);
    }

    @Override
    public List<Tache> getUnassignedTaches() {
        return tacheRepository.findByAnnotatorIsNull();
    }

    @Override
    public double getTacheProgress(Long tacheId) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new IllegalArgumentException("Tache not found"));
        int totalPairs = tache.getTextPairs().size();
        if (totalPairs == 0) return 0.0;
        
        int annotatedPairs = (int) tache.getTextPairs().stream()
                .filter(TextPair::isAnnotated)
                .count();
        return (double) annotatedPairs / totalPairs;
    }

    @Override
    public int getTotalTextPairs(Long tacheId) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new IllegalArgumentException("Tache not found"));
        return tache.getTextPairs().size();
    }

    @Override
    public int getAnnotatedTextPairs(Long tacheId) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new IllegalArgumentException("Tache not found"));
        return (int) tache.getTextPairs().stream()
                .filter(TextPair::isAnnotated)
                .count();
    }

    @Override
    public List<Tache> getTachesByDeadline(Date date) {
        return tacheRepository.findByDateLimiteBefore(date);
    }

    @Override
    public List<Tache> getOverdueTaches() {
        return tacheRepository.findByDateLimiteBefore(new Date());
    }

    @Override
    public List<Tache> getUpcomingTaches(int days) {
        Date today = new Date();
        Date upcomingDate = new Date(today.getTime() + (long) days * 24 * 60 * 60 * 1000);
        return tacheRepository.findByDateLimiteBetween(today, upcomingDate);
    }

    @Override
    public List<Tache> assignTasksToAnnotators(List<Long> annotatorIds) {
        if (annotatorIds == null || annotatorIds.isEmpty()) {
            throw new IllegalArgumentException("No annotators selected");
        }

        List<Annotator> annotators = annotatorRepository.findAllById(annotatorIds);
        if (annotators.isEmpty()) {
            throw new IllegalArgumentException("No valid annotators found");
        }

        List<TextPair> unassignedTextPairs = textPairRepository.findByTacheIsNull();
        if (unassignedTextPairs.isEmpty()) {
            throw new IllegalStateException("No unassigned text pairs available");
        }

        List<Tache> createdTasks = new ArrayList<>();
        int currentIndex = 0;

        for (Annotator annotator : annotators) {
            if (currentIndex >= unassignedTextPairs.size()) {
                break;
            }

            int pairsToAssign = Math.min(pairsPerAnnotator, unassignedTextPairs.size() - currentIndex);
            List<TextPair> annotatorPairs = new ArrayList<>(unassignedTextPairs.subList(currentIndex, currentIndex + pairsToAssign));
            currentIndex += pairsToAssign;

            Tache tache = new Tache();
            tache.setAnnotator(annotator);
            tache.setDateLimite(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)); // 7 jours
            tache.setTextPairs(new HashSet<>(annotatorPairs));
            tache.setDataset(annotatorPairs.get(0).getDataset());

            for (TextPair tp : annotatorPairs) {
                tp.setTache(tache);
                tp.setDataset(tache.getDataset());
            }

            tacheRepository.save(tache);
            log.info("Assigned {} pairs to annotator {} with task ID {}",
                    annotatorPairs.size(), annotator.getIdUser(), tache.getIdTache());

            createdTasks.add(tache);
        }

        return createdTasks;
    }

    @Override
    public List<Tache> getTasksForAnnotator(Annotator annotator) {
        return tacheRepository.findByAnnotator(annotator);
    }

    @Override
    public List<Tache> getUnassignedTasks() {
        return tacheRepository.findByAnnotatorIsNull();
    }

    @Override
    public int getRemainingUnassignedPairs() {

        System.out.println("\n===  tttttttttttttttttttttttttttttttttttt===");

        List<TextPair> allPairs = textPairRepository.findAll();
        System.out.println("Nombre total de paires de texte: " + allPairs.size());
        List<Tache> allTasks = tacheRepository.findAll();
        System.out.println("Nombre total de tâches: " + allTasks.size());
        Set<Long> assignedPairIds = allTasks.stream()
                .flatMap(task -> task.getTextPairs().stream())
                .map(TextPair::getIdTextPair)
                .collect(Collectors.toSet());

        System.out.println("Nombre de paires déjà assignées: " + assignedPairIds.size());

        int unassignedPairs = (int) allPairs.stream()
                .filter(pair -> !assignedPairIds.contains(pair.getIdTextPair()))
                .count();

        System.out.println("Nombre de paires non assignées: " + unassignedPairs);

        return unassignedPairs;
    }


    @Override
    @Transactional
    public void assignTasksToAnnotators(Long datasetId, List<Long> annotatorIds) {
        Dataset dataset = datasetService.getDatasetById(datasetId);
        if (dataset == null) {
            throw new RuntimeException("Dataset not found: " + datasetId);
        }

        List<TextPair> unassignedPairs = textPairRepository.findByDataset_IdDatasetAndTacheIsNull(datasetId);
        if (unassignedPairs.isEmpty()) {
            throw new RuntimeException("No unassigned pairs available for dataset: " + datasetId);
        }

        // Calculate pairs per annotator
//        int pairsPerAnnotator = 20;
        int pairsPerAnnotator = unassignedPairs.size() / annotatorIds.size();
        int remainingPairs = unassignedPairs.size() % annotatorIds.size();

        int currentPairIndex = 0;
        for (Long annotatorId : annotatorIds) {
            Annotator annotator = annotatorService.findById(annotatorId);
            if (annotator == null) {
                throw new RuntimeException("Annotator not found: " + annotatorId);
            }

            // Create task for this annotator
            Tache tache = new Tache();
            tache.setDataset(dataset);
            tache.setAnnotator(annotator);
            tache.setDateDebut(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            tache.setDateLimite(Date.from(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()));
            tache.setStatut(StatutTache.NOT_STARTED);

            // Assign pairs to this task
            int pairsToAssign = pairsPerAnnotator + (remainingPairs > 0 ? 1 : 0);
            remainingPairs--;

            for (int i = 0; i < pairsToAssign && currentPairIndex < unassignedPairs.size(); i++) {
                TextPair pair = unassignedPairs.get(currentPairIndex++);
                pair.setTache(tache);
                textPairRepository.save(pair);
            }

            tacheRepository.save(tache);
        }
    }

    @Override
    @Transactional
    public void removeAnnotatorFromDataset(Long datasetId, Long annotatorId) {
        // Find all tasks for this annotator and dataset
        List<Tache> tasks = tacheRepository.findByDataset_IdDatasetAndAnnotator_IdUser(datasetId, annotatorId);
        
        for (Tache task : tasks) {
            // Unassign all text pairs from this task
            Set<TextPair> pairs = task.getTextPairs();
            for (TextPair pair : pairs) {
                pair.setTache(null);
                textPairRepository.save(pair);
            }
            
            // Delete the task
            tacheRepository.delete(task);
        }
    }

    @Override
    public List<Tache> getUnassignedTasks(Long datasetId) {
        return tacheRepository.findByDataset_IdDatasetAndAnnotatorIsNull(datasetId);
    }

    @Override
    public int countCompletedTasks() {
        // Supposons que le statut COMPLETED existe
        return tacheRepository.countByStatut(StatutTache.COMPLETED);
    }

    @Override
    public int getAverageProgress() {
        // Calculer la moyenne des pourcentages de complétion de toutes les tâches
        List<Tache> allTasks = tacheRepository.findAll();
        if (allTasks.isEmpty()) return 0;
        double sum = 0;
        for (Tache t : allTasks) {
            sum += t.getPourcentageComplet();
        }
        return (int) Math.round(sum / allTasks.size());
    }
} 