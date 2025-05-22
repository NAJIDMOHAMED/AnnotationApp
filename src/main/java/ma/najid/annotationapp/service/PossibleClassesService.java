package ma.najid.annotationapp.service;

import ma.najid.annotationapp.Model.PossibleClasses;
import ma.najid.annotationapp.Model.Dataset;
import java.util.List;
import java.util.Optional;

public interface PossibleClassesService {
    // Basic CRUD operations
    PossibleClasses savePossibleClass(PossibleClasses possibleClass);
    List<PossibleClasses> getAllPossibleClasses();
    Optional<PossibleClasses> getPossibleClassById(Long id);
    void deletePossibleClass(Long id);
    
    // Business operations
    List<PossibleClasses> getPossibleClassesByDataset(Dataset dataset);
    List<PossibleClasses> getPossibleClassesByDatasetId(Long datasetId);
    boolean existsByTypeClassAndDataset(String typeClass, Dataset dataset);
    void deletePossibleClassesByDataset(Dataset dataset);
    
    // Batch operations
    List<PossibleClasses> saveAllPossibleClasses(List<PossibleClasses> possibleClasses);
    void deleteAllPossibleClasses(List<PossibleClasses> possibleClasses);
} 