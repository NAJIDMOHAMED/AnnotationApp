package ma.najid.annotationapp.service;

import ma.najid.annotationapp.Model.Annotator;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

public interface AnnotatorService {
    Annotator saveAnnotator(Annotator annotator);
    List<Annotator> getAllAnnotators();
    List<Annotator> getAnnotatorsByDataset(Long datasetId);
    List<Annotator> getAvailableAnnotators(Long datasetId);
    Annotator findById(Long id);
    Optional<Annotator> getAnnotatorById(Long id);
    Optional<Annotator> getAnnotatorByEmail(String email);
    Annotator updateAnnotator(Long id, Annotator annotatorDetails);
    void deleteAnnotator(Long id);
    boolean existsByEmail(String email);

    boolean existsById(Long id);

    void prepareAnnotatorHomeData(Annotator annotator, Model model);
} 