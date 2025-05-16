package ma.najid.annotationapp.service;

import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.Tache;
import ma.najid.annotationapp.Model.TextPair;
import ma.najid.annotationapp.repository.AnnotatorRepository;
import ma.najid.annotationapp.repository.TacheRepository;
import ma.najid.annotationapp.repository.TextPairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TacheService {
    private static final int PAIRS_PER_ANNOTATOR = 20;

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private AnnotatorRepository annotatorRepository;

    @Autowired
    private TextPairRepository textPairRepository;

    @Transactional
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
            // Vérifier s'il reste assez de paires de texte à assigner
            if (currentIndex >= unassignedTextPairs.size()) {
                break;
            }

            // Calculer le nombre de paires à assigner (maximum 20)
            int pairsToAssign = Math.min(PAIRS_PER_ANNOTATOR, unassignedTextPairs.size() - currentIndex);

            // Sélectionner les paires de texte pour cet annotateur
            List<TextPair> annotatorPairs = unassignedTextPairs.subList(
                currentIndex, 
                currentIndex + pairsToAssign
            );
            currentIndex += pairsToAssign;

            // Créer une nouvelle tâche
            Tache tache = new Tache();
            tache.setAnnotator(annotator);
            tache.setDateLimite(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)); // 7 jours
            tache.setTextPairs(new HashSet<>(annotatorPairs));
            tache.setDataset(annotatorPairs.get(0).getDataset());

            // Mettre à jour les relations
            for (TextPair tp : annotatorPairs) {
                tp.setTache(tache);
            }

            // Sauvegarder la tâche
            tacheRepository.save(tache);
            createdTasks.add(tache);
        }

        return createdTasks;
    }

    public List<Tache> getTasksForAnnotator(Annotator annotator) {
        return tacheRepository.findByAnnotator(annotator);
    }

    public List<Tache> getUnassignedTasks() {
        return tacheRepository.findByAnnotatorIsNull();
    }

    public List<Tache> getAllTasks() {
        return tacheRepository.findAll();
    }

    public int getRemainingUnassignedPairs() {
        return textPairRepository.findByTacheIsNull().size();
    }
} 