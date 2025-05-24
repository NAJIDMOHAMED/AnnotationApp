package ma.najid.annotationapp.repository;

import ma.najid.annotationapp.Model.Annotation;
import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.TextPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
    List<Annotation> findByAnnotator_IdUser(Long annotatorId);
    List<Annotation> findByTextPair_IdTextPair(Long textPairId);
    List<Annotation> findByPossibleClass_IdPossibleClass(Long possibleClassId);
    List<Annotation> findByAnnotator_IdUserAndTextPair_IdTextPair(Long annotatorId, Long textPairId);

    boolean existsByTextPairAndAnnotator(TextPair textPair, Annotator annotator);
} 