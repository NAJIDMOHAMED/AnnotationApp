package ma.najid.annotationapp.repository;

import ma.najid.annotationapp.Model.Role;
import ma.najid.annotationapp.Model.TYPES.TypeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNomRole(TypeRole nomRole);
} 