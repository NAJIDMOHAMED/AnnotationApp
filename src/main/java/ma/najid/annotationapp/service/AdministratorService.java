package ma.najid.annotationapp.service;

import ma.najid.annotationapp.Model.Administrator;
import java.util.List;
import java.util.Optional;

public interface AdministratorService {
    Administrator saveAdministrator(Administrator administrator);
    List<Administrator> getAllAdministrators();
    Optional<Administrator> getAdministratorById(Long id);
    Optional<Administrator> getAdministratorByEmail(String email);
    Administrator updateAdministrator(Long id, Administrator administratorDetails);
    void deleteAdministrator(Long id);
    boolean existsByEmail(String email);
}