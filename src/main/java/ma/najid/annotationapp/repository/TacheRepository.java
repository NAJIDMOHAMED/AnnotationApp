package ma.najid.annotationapp.repository;

import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Long> {
    // Find tasks by dataset
    List<Tache> findByDataset_IdDataset(Long datasetId);
    
    // Find tasks by annotator
    List<Tache> findByAnnotator(Annotator annotator);
    List<Tache> findByAnnotator_IdUser(Long idUser);
    
    // Find tasks with deadline before a specific date
    List<Tache> findByDateLimiteBefore(Date date);
    
    // Find tasks between two dates
    List<Tache> findByDateLimiteBetween(Date startDate, Date endDate);
    
    // Find tasks that are not assigned to any annotator
    List<Tache> findByAnnotatorIsNull();
    
    // Find tasks by dataset and annotator
    List<Tache> findByDataset_IdDatasetAndAnnotator_IdUser(Long datasetId, Long idUser);
    
    // Find unassigned tasks for a specific dataset
    List<Tache> findByDataset_IdDatasetAndAnnotatorIsNull(Long datasetId);
} 