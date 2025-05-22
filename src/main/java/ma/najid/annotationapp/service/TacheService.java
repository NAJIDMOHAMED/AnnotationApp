package ma.najid.annotationapp.service;

import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.Tache;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TacheService {
    // Opérations CRUD de base
    Tache saveTache(Tache tache);
    List<Tache> getAllTasks();
    Optional<Tache> getTacheById(Long id);
    void deleteTache(Long id);

    // Opérations métier spécifiques
    List<Tache> getTachesByAnnotator(Annotator annotator);
    List<Tache> getTachesByAnnotatorId(Long annotatorId);
    List<Tache> getTachesByDatasetId(Long datasetId);
    List<Tache> getUnassignedTaches();

    // Opérations de gestion des annotations
    double getTacheProgress(Long tacheId);
    int getTotalTextPairs(Long tacheId);
    int getAnnotatedTextPairs(Long tacheId);

    // Opérations de gestion des dates
    List<Tache> getTachesByDeadline(Date date);
    List<Tache> getOverdueTaches();
    List<Tache> getUpcomingTaches(int days);

    // Opérations d'assignation
    List<Tache> assignTasksToAnnotators(List<Long> annotatorIds);
    List<Tache> getTasksForAnnotator(Annotator annotator);
    List<Tache> getUnassignedTasks();
    int getRemainingUnassignedPairs();

    void assignTasksToAnnotators(Long datasetId, List<Long> annotatorIds);
    void removeAnnotatorFromDataset(Long datasetId, Long annotatorId);
    List<Tache> getUnassignedTasks(Long datasetId);
} 