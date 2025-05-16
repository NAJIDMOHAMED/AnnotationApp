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
    private AdministratorServiceImpl administratorService;

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
        Administrator administrator = new Administrator();
        administrator.setNom("Admin");
        administrator.setPrenom("Admin");
        administrator.setEmail("admin123@gmail.com");
        administrator.setPassword("admin123");
        administrator.setRole(roleRepository.findByNomRole(TypeRole.ADMIN_ROLE).get());
        administratorService.saveAdministrator(administrator);


        Annotator annotator = new Annotator();
        annotator.setNom("test");
        annotator.setPrenom("Annotator");
        annotator.setEmail("test@gmail.com");
        annotator.setPassword("123");
        annotator.setRole(roleRepository.findByNomRole(TypeRole.ANNOTATOR_ROLE).get());
        annotatorServiceImpl.saveAnnotator(annotator);

        Annotator annotator1=new Annotator();
        annotator1.setNom("test1");
        annotator1.setPrenom("test1");
        annotator1.setEmail("test1@gmail.com");
        annotator1.setPassword("123");
        annotator1.setRole(roleRepository.findByNomRole(TypeRole.ANNOTATOR_ROLE).get());
        annotatorServiceImpl.saveAnnotator(annotator1);


    }
} 