package ma.najid.annotationapp.repository;

import ma.najid.annotationapp.Model.Dataset;
import ma.najid.annotationapp.Model.PossibleClasses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PossibleClassesRepository extends JpaRepository<PossibleClasses, Long> {
    List<PossibleClasses> findByDataset(Dataset dataset);

    List<PossibleClasses> findByDataset_IdDataset(Long datasetId);

    boolean existsByTypeClassAndDataset(String typeClass, Dataset dataset);

    void deleteByDataset(Dataset dataset);
    // Basic CRUD operations are automatically provided by JpaRepository
    // You can add custom query methods here if needed
} 