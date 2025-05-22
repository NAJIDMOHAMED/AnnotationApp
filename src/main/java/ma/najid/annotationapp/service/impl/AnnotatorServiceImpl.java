package ma.najid.annotationapp.service.impl;

import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.Model.Role;
import ma.najid.annotationapp.Model.TYPES.TypeRole;
import ma.najid.annotationapp.Model.TYPES.StatutTache;
import ma.najid.annotationapp.Model.Tache;
import ma.najid.annotationapp.Model.Dataset;
import ma.najid.annotationapp.repository.AnnotatorRepository;
import ma.najid.annotationapp.repository.RoleRepository;
import ma.najid.annotationapp.repository.TacheRepository;
import ma.najid.annotationapp.service.AnnotatorService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AnnotatorServiceImpl implements AnnotatorService {

    private final AnnotatorRepository annotatorRepository;
    private final RoleRepository roleRepository;
    private final TacheRepository tacheRepository;

    @Autowired
    public AnnotatorServiceImpl(AnnotatorRepository annotatorRepository, RoleRepository roleRepository, TacheRepository tacheRepository) {
        this.annotatorRepository = annotatorRepository;
        this.roleRepository = roleRepository;
        this.tacheRepository = tacheRepository;
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
    public List<Annotator> getAnnotatorsByDataset(Long datasetId) {
        List<Annotator> annotators = annotatorRepository.findByTaches_Dataset_IdDataset(datasetId);
        System.out.println("Annotateurs assignés au dataset " + datasetId + ":");
        for (Annotator annotator : annotators) {
            System.out.println("- " + annotator.getEmail() + 
                             " (Rôle: " + (annotator.getRole() != null ? annotator.getRole().getNomRole() : "null") + ")");
        }
        return annotators;
    }

    @Override
    public List<Annotator> getAvailableAnnotators(Long datasetId) {
        System.out.println("Recherche des annotateurs disponibles pour le dataset: " + datasetId);
        
        // Vérifier d'abord tous les annotateurs
        List<Annotator> allAnnotators = annotatorRepository.findAll();
        System.out.println("Nombre total d'annotateurs: " + allAnnotators.size());
        
        // Vérifier les annotateurs avec le rôle ANNOTATOR_ROLE
        List<Annotator> annotatorsWithRole = allAnnotators.stream()
            .filter(a -> a.getRole() != null && "ANNOTATOR_ROLE".equals(a.getRole().getNomRole()))
            .toList();
        System.out.println("Nombre d'annotateurs avec le rôle ANNOTATOR_ROLE: " + annotatorsWithRole.size());
        
        // Obtenir les annotateurs disponibles
        List<Annotator> availableAnnotators = annotatorRepository.findAvailableAnnotators(datasetId);
        System.out.println("Nombre d'annotateurs disponibles: " + availableAnnotators.size());
        
        for (Annotator annotator : availableAnnotators) {
            System.out.println("Annotateur disponible: " + annotator.getEmail() + 
                             " (Rôle: " + (annotator.getRole() != null ? annotator.getRole().getNomRole() : "null") + ")");
        }
        
        return availableAnnotators;
    }

    @Override
    public Annotator findById(Long id) {
        return annotatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annotator not found with id: " + id));
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

    @Override
    public boolean existsById(Long id) {
        return annotatorRepository.existsById(id);
    }

    public void prepareAnnotatorHomeData(Annotator annotator, Model model) {
        Hibernate.initialize(annotator.getAnnotations());

        List<Tache> taches = tacheRepository.findByAnnotator_IdUser(annotator.getIdUser());

        for (Tache tache : taches) {
            if (tache.getDataset() != null) {
                Hibernate.initialize(tache.getDataset());
            }
        }

        long completed = taches.stream().filter(t -> t.getStatut() == StatutTache.COMPLETED).count();
        long inProgress = taches.stream().filter(t -> t.getStatut() == StatutTache.IN_PROGRESS).count();

        model.addAttribute("taches", taches);
        model.addAttribute("completedCount", completed);
        model.addAttribute("inProgressCount", inProgress);
        model.addAttribute("totalCount", taches.size());

        if (taches.isEmpty()) {
            model.addAttribute("noTaskMessage", "Vous n'avez aucune tâche à annoter pour le moment.");
        }
    }
} 