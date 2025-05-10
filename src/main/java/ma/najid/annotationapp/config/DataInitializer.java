package ma.najid.annotationapp.config;

import ma.najid.annotationapp.Model.Role;
import ma.najid.annotationapp.Model.TYPES.TypeRole;
import ma.najid.annotationapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Autowired
    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        // Initialize roles if they don't exist
        initializeRoles();
    }

    private void initializeRoles() {
        // Check and create ADMIN_ROLE
        if (!roleRepository.findByNomRole(TypeRole.ADMIN_ROLE).isPresent()) {
            Role adminRole = new Role();
            adminRole.setNomRole(TypeRole.ADMIN_ROLE);
            roleRepository.save(adminRole);
        }

        // Check and create ANNOTATOR_ROLE
        if (!roleRepository.findByNomRole(TypeRole.ANNOTATOR_ROLE).isPresent()) {
            Role annotatorRole = new Role();
            annotatorRole.setNomRole(TypeRole.ANNOTATOR_ROLE);
            roleRepository.save(annotatorRole);
        }
    }
} 