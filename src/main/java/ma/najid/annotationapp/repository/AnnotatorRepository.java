package ma.najid.annotationapp.repository;

import ma.najid.annotationapp.Model.Annotator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnotatorRepository extends JpaRepository<Annotator, Long> {
    Optional<Annotator> findByEmail(String email);
    boolean existsByEmail(String email);

    List<Annotator> findByTaches_Dataset_IdDataset(Long datasetId);

    @Query("""
    SELECT DISTINCT a FROM Annotator a 
    WHERE a.role.nomRole = 'ANNOTATOR_ROLE' 
    AND NOT EXISTS (
        SELECT t FROM Tache t 
        WHERE t.annotator = a 
        AND t.dataset.idDataset = :datasetId
    )
    """)
    List<Annotator> findAvailableAnnotators(@Param("datasetId") Long datasetId);

    @Query("SELECT a FROM Annotator a WHERE a.role.nomRole = 'ANNOTATOR_ROLE'")
    List<Annotator> findAllAnnotators();

    @Query("SELECT a FROM Annotator a LEFT JOIN FETCH a.role")
    List<Annotator> findAllWithRoles();
} 