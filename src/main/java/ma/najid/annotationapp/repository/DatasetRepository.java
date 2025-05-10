package ma.najid.annotationapp.repository;

import ma.najid.annotationapp.Model.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    List<Dataset> findByNomContaining(String nom);
    boolean existsByNom(String nom);
} 