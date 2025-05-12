package ma.najid.annotationapp.service.impl;

import ma.najid.annotationapp.Model.Administrator;
import ma.najid.annotationapp.Model.Role;
import ma.najid.annotationapp.Model.TYPES.TypeRole;
import ma.najid.annotationapp.repository.AdministratorRepository;
import ma.najid.annotationapp.repository.RoleRepository;
import ma.najid.annotationapp.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdministratorServiceImpl implements AdministratorService {

    private final AdministratorRepository administratorRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AdministratorServiceImpl(AdministratorRepository administratorRepository, RoleRepository roleRepository) {
        this.administratorRepository = administratorRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Administrator saveAdministrator(Administrator administrator) {
        // Set default role for administrator
        Role administratorRole = roleRepository.findByNomRole(TypeRole.ADMIN_ROLE)
                .orElseThrow(() -> new RuntimeException("ADMINISTRATOR_ROLE not found"));
        administrator.setRole(administratorRole);
        return administratorRepository.save(administrator);
    }

    @Override
    public List<Administrator> getAllAdministrators() {
        return administratorRepository.findAll();
    }

    @Override
    public Optional<Administrator> getAdministratorById(Long id) {
        return administratorRepository.findById(id);
    }

    @Override
    public Optional<Administrator> getAdministratorByEmail(String email) {
        return administratorRepository.findByEmail(email);
    }

    @Override
    public Administrator updateAdministrator(Long id, Administrator administratorDetails) {
        return administratorRepository.findById(id)
                .map(existingAdministrator -> {
                    existingAdministrator.setNom(administratorDetails.getNom());
                    existingAdministrator.setPrenom(administratorDetails.getPrenom());
                    existingAdministrator.setEmail(administratorDetails.getEmail());
                    existingAdministrator.setPassword(administratorDetails.getPassword());
                    existingAdministrator.setRole(administratorDetails.getRole());
                    return administratorRepository.save(existingAdministrator);
                })
                .orElseThrow(() -> new RuntimeException("Administrator not found with id: " + id));
    }

    @Override
    public void deleteAdministrator(Long id) {
        administratorRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return administratorRepository.existsByEmail(email);
    }



} 