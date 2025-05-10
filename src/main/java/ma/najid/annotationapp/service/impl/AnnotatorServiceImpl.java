package ma.najid.annotationapp.service.impl;

import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.Role;
import ma.najid.annotationapp.Model.TYPES.TypeRole;
import ma.najid.annotationapp.repository.AnnotatorRepository;
import ma.najid.annotationapp.repository.RoleRepository;
import ma.najid.annotationapp.service.AnnotatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AnnotatorServiceImpl implements AnnotatorService {

    private final AnnotatorRepository annotatorRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AnnotatorServiceImpl(AnnotatorRepository annotatorRepository, RoleRepository roleRepository) {
        this.annotatorRepository = annotatorRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Annotator saveAnnotator(Annotator annotator) {
        // Set default role for annotator
        Role annotatorRole = roleRepository.findByNomRole(TypeRole.ANNOTATOR_ROLE)
                .orElseThrow(() -> new RuntimeException("ANNOTATOR_ROLE not found"));
        annotator.setRole(annotatorRole);
        return annotatorRepository.save(annotator);
    }

    @Override
    public List<Annotator> getAllAnnotators() {
        return annotatorRepository.findAll();
    }

    @Override
    public Optional<Annotator> getAnnotatorById(Long id) {
        return annotatorRepository.findById(id);
    }

    @Override
    public Optional<Annotator> getAnnotatorByEmail(String email) {
        return annotatorRepository.findByEmail(email);
    }

    @Override
    public Annotator updateAnnotator(Long id, Annotator annotatorDetails) {
        return annotatorRepository.findById(id)
                .map(existingAnnotator -> {
                    existingAnnotator.setNom(annotatorDetails.getNom());
                    existingAnnotator.setPrenom(annotatorDetails.getPrenom());
                    existingAnnotator.setEmail(annotatorDetails.getEmail());
                    existingAnnotator.setPassword(annotatorDetails.getPassword());
                    existingAnnotator.setRole(annotatorDetails.getRole());
                    return annotatorRepository.save(existingAnnotator);
                })
                .orElseThrow(() -> new RuntimeException("Annotator not found with id: " + id));
    }

    @Override
    public void deleteAnnotator(Long id) {
        annotatorRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return annotatorRepository.existsByEmail(email);
    }
} 