package ma.najid.annotationapp.repository;

import ma.najid.annotationapp.Model.TextPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextPairRepository extends JpaRepository<TextPair, Long> {
    List<TextPair> findByTacheIsNull();
    List<TextPair> findByDataset_IdDataset(Long datasetId);
    List<TextPair> findByText1ContainingOrText2Containing(String text1, String text2);
} 