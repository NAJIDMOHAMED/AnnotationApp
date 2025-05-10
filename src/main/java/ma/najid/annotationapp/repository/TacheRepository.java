package ma.najid.annotationapp.repository;

import ma.najid.annotationapp.Model.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Long> {
    List<Tache> findByAnnotator_IdUser(Long annotatorId);
    List<Tache> findByDataset_IdDataset(Long datasetId);
    List<Tache> findByDateLimiteBefore(Date date);
    List<Tache> findByAnnotator_IdUserAndDataset_IdDataset(Long annotatorId, Long datasetId);
} 