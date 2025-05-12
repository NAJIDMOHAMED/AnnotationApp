package ma.najid.annotationapp.repository;

import ma.najid.annotationapp.Model.TYPES.TypeRole;
import ma.najid.annotationapp.Model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserAccount> findByEmailAndPassword(String email, String password);
    List<UserAccount> findAllByRole_NomRole(TypeRole nomRole);
} 