package ma.najid.annotationapp.repository;

import ma.najid.annotationapp.Model.Annotator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnnotatorRepository extends JpaRepository<Annotator, Long> {
    Optional<Annotator> findByEmail(String email);
    boolean existsByEmail(String email);

} 