package ma.najid.annotationapp.repository;

import ma.najid.annotationapp.Model.PossibleClasses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PossibleClassesRepository extends JpaRepository<PossibleClasses, Long> {
    // Basic CRUD operations are automatically provided by JpaRepository
    // You can add custom query methods here if needed
} 