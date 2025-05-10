package ma.najid.annotationapp.service;

import ma.najid.annotationapp.Model.Annotator;
import java.util.List;
import java.util.Optional;

public interface AnnotatorService {
    Annotator saveAnnotator(Annotator annotator);
    List<Annotator> getAllAnnotators();
    Optional<Annotator> getAnnotatorById(Long id);
    Optional<Annotator> getAnnotatorByEmail(String email);
    Annotator updateAnnotator(Long id, Annotator annotatorDetails);
    void deleteAnnotator(Long id);
    boolean existsByEmail(String email);
} 