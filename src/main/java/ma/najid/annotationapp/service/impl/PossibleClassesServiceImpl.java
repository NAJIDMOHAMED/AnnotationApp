package ma.najid.annotationapp.service.impl;

import ma.najid.annotationapp.Model.PossibleClasses;
import ma.najid.annotationapp.Model.Dataset;
import ma.najid.annotationapp.repository.PossibleClassesRepository;
import ma.najid.annotationapp.service.PossibleClassesService;
import ma.najid.annotationapp.exception.AnnotationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PossibleClassesServiceImpl implements PossibleClassesService {
    
    private static final Logger logger = LoggerFactory.getLogger(PossibleClassesServiceImpl.class);
    
    private final PossibleClassesRepository possibleClassesRepository;
    
    @Autowired
    public PossibleClassesServiceImpl(PossibleClassesRepository possibleClassesRepository) {
        this.possibleClassesRepository = possibleClassesRepository;
    }
    
    @Override
    public PossibleClasses savePossibleClass(PossibleClasses possibleClass) {
        try {
            logger.info("Saving possible class: {}", possibleClass.getTypeClass());
            return possibleClassesRepository.save(possibleClass);
        } catch (Exception e) {
            logger.error("Error saving possible class: {}", e.getMessage(), e);
            throw new AnnotationException("Error saving possible class", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PossibleClasses> getAllPossibleClasses() {
        try {
            logger.info("Fetching all possible classes");
            return possibleClassesRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all possible classes: {}", e.getMessage(), e);
            throw new AnnotationException("Error fetching possible classes", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PossibleClasses> getPossibleClassById(Long id) {
        try {
            logger.info("Fetching possible class with id: {}", id);
            return possibleClassesRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error fetching possible class with id {}: {}", id, e.getMessage(), e);
            throw new AnnotationException("Error fetching possible class", e);
        }
    }
    
    @Override
    public void deletePossibleClass(Long id) {
        try {
            logger.info("Deleting possible class with id: {}", id);
            possibleClassesRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting possible class with id {}: {}", id, e.getMessage(), e);
            throw new AnnotationException("Error deleting possible class", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PossibleClasses> getPossibleClassesByDataset(Dataset dataset) {
        try {
            logger.info("Fetching possible classes for dataset: {}", dataset.getIdDataset());
            return possibleClassesRepository.findByDataset(dataset);
        } catch (Exception e) {
            logger.error("Error fetching possible classes for dataset {}: {}", 
                        dataset.getIdDataset(), e.getMessage(), e);
            throw new AnnotationException("Error fetching possible classes for dataset", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PossibleClasses> getPossibleClassesByDatasetId(Long datasetId) {
        try {
            logger.info("Fetching possible classes for dataset id: {}", datasetId);
            return possibleClassesRepository.findByDataset_IdDataset(datasetId);
        } catch (Exception e) {
            logger.error("Error fetching possible classes for dataset id {}: {}", 
                        datasetId, e.getMessage(), e);
            throw new AnnotationException("Error fetching possible classes for dataset", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByTypeClassAndDataset(String typeClass, Dataset dataset) {
        try {
            logger.info("Checking if possible class exists: {} for dataset: {}", 
                       typeClass, dataset.getIdDataset());
            return possibleClassesRepository.existsByTypeClassAndDataset(typeClass, dataset);
        } catch (Exception e) {
            logger.error("Error checking possible class existence: {}", e.getMessage(), e);
            throw new AnnotationException("Error checking possible class existence", e);
        }
    }
    
    @Override
    public void deletePossibleClassesByDataset(Dataset dataset) {
        try {
            logger.info("Deleting all possible classes for dataset: {}", dataset.getIdDataset());
            possibleClassesRepository.deleteByDataset(dataset);
        } catch (Exception e) {
            logger.error("Error deleting possible classes for dataset {}: {}", 
                        dataset.getIdDataset(), e.getMessage(), e);
            throw new AnnotationException("Error deleting possible classes for dataset", e);
        }
    }
    
    @Override
    public List<PossibleClasses> saveAllPossibleClasses(List<PossibleClasses> possibleClasses) {
        try {
            logger.info("Saving {} possible classes", possibleClasses.size());
            return possibleClassesRepository.saveAll(possibleClasses);
        } catch (Exception e) {
            logger.error("Error saving possible classes: {}", e.getMessage(), e);
            throw new AnnotationException("Error saving possible classes", e);
        }
    }
    
    @Override
    public void deleteAllPossibleClasses(List<PossibleClasses> possibleClasses) {
        try {
            logger.info("Deleting {} possible classes", possibleClasses.size());
            possibleClassesRepository.deleteAll(possibleClasses);
        } catch (Exception e) {
            logger.error("Error deleting possible classes: {}", e.getMessage(), e);
            throw new AnnotationException("Error deleting possible classes", e);
        }
    }
} 