package ma.najid.annotationapp.config;

import ma.najid.annotationapp.Model.Administrator;
import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.Role;
import ma.najid.annotationapp.Model.TYPES.TypeRole;
import ma.najid.annotationapp.repository.RoleRepository;
import ma.najid.annotationapp.service.impl.AdministratorServiceImpl;
import ma.najid.annotationapp.service.impl.AnnotatorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AnnotatorServiceImpl annotatorServiceImpl;
    private final AdministratorServiceImpl administratorService;

    @Autowired
    public DataInitializer(RoleRepository roleRepository, AdministratorServiceImpl administratorService, AnnotatorServiceImpl annotatorServiceImpl) {
        this.roleRepository = roleRepository;
        this.administratorService = administratorService;
        this.annotatorServiceImpl = annotatorServiceImpl;
    }

    @Override
    public void run(String... args) {
        // Initialize roles if they don't exist
        initializeRoles();
    }

    private void initializeRoles() {
        // Vérifier si le rôle ADMIN existe déjà
        Role adminRole = roleRepository.findByNomRole(TypeRole.ADMIN_ROLE)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setNomRole(TypeRole.ADMIN_ROLE);
                    return roleRepository.save(role);
                });

        // Vérifier si l'administrateur existe déjà
        if (!administratorService.existsByEmail("admin123@gmail.com")) {
            Administrator admin = new Administrator();
            admin.setNom("Admin");
            admin.setPrenom("Admin");
            admin.setEmail("admin123@gmail.com");
            admin.setPassword("1234"); // Mot de passe en clair pour le test
            admin.setEnabled(true);
            admin.setRole(adminRole);
            administratorService.saveAdministrator(admin);
        }

        // Vérifier si le rôle ANNOTATOR existe déjà
        Role annotatorRole = roleRepository.findByNomRole(TypeRole.ANNOTATOR_ROLE)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setNomRole(TypeRole.ANNOTATOR_ROLE);
                    return roleRepository.save(role);
                });

        // Vérifier si le premier annotateur existe déjà
        if (!annotatorServiceImpl.existsByEmail("test3@gmail.com")) {
            Annotator annotator = new Annotator();
            annotator.setNom("test");
            annotator.setPrenom("Annotator");
            annotator.setEmail("test3@gmail.com");
            annotator.setPassword("123"); // Mot de passe en clair pour le test
            annotator.setEnabled(true);
            annotator.setRole(annotatorRole);
            annotatorServiceImpl.saveAnnotator(annotator);
        }

        // Vérifier si le deuxième annotateur existe déjà
        if (!annotatorServiceImpl.existsByEmail("test12@gmail.com")) {
            Annotator annotator1 = new Annotator();
            annotator1.setNom("test1");
            annotator1.setPrenom("test1");
            annotator1.setEmail("test12@gmail.com");
            annotator1.setPassword("1234"); // Mot de passe en clair pour le test
            annotator1.setEnabled(true);
            annotator1.setRole(annotatorRole);
            annotatorServiceImpl.saveAnnotator(annotator1);
        }
    }
} 