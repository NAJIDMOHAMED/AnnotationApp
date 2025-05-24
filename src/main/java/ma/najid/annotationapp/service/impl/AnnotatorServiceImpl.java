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
        System.out.println("\n=== DÉBUT DU DÉBOGAGE DES ANNOTATEURS ===");
        List<Annotator> annotators = annotatorRepository.findAllAnnotators();
        System.out.println("Nombre total d'annotateurs trouvés: " + annotators.size());
        
        // Afficher les détails de chaque annotateur pour le débogage
        annotators.forEach(annotator -> {
            System.out.println("Annotateur: " + annotator.getIdUser() + 
                             " - Email: " + annotator.getEmail() + 
                             " - Nom: " + annotator.getNom() + 
                             " - Prénom: " + annotator.getPrenom() +
                             " - Rôle: " + (annotator.getRole() != null ? annotator.getRole().getNomRole() : "null"));
        });
        
        System.out.println("=== FIN DU DÉBOGAGE DES ANNOTATEURS ===\n");
        return annotators;
    }

    @Override
    public List<Annotator> getAnnotatorsByDataset(Long datasetId) {
        System.out.println("Recherche des annotateurs pour le dataset: " + datasetId);
        List<Annotator> annotators = annotatorRepository.findByTaches_Dataset_IdDataset(datasetId);
        System.out.println("Nombre d'annotateurs trouvés: " + annotators.size());
        
        // Vérifier les détails de chaque annotateur
        for (Annotator annotator : annotators) {
            System.out.println("Annotateur trouvé: " + annotator.getIdUser() + 
                             " - Email: " + annotator.getEmail() + 
                             " - Nom: " + annotator.getNom() + 
                             " - Prénom: " + annotator.getPrenom());
            
            // Vérifier les tâches de l'annotateur
            if (annotator.getTaches() != null) {
                System.out.println("Nombre de tâches: " + annotator.getTaches().size());
                annotator.getTaches().forEach(tache -> 
                    System.out.println("  - Tâche ID: " + tache.getIdTache() + 
                                     " - Dataset ID: " + tache.getDataset().getIdDataset()));
            } else {
                System.out.println("Aucune tâche trouvée pour cet annotateur");
            }
        }
        
        return annotators;
    }

    @Override
    public List<Annotator> getAvailableAnnotators(Long datasetId) {
        System.out.println("\n=== DÉBUT DU DÉBOGAGE DES ANNOTATEURS (SANS FILTRAGE PAR TÂCHE) ===");

        // Récupérer tous les annotateurs avec leurs rôles
        List<Annotator> allAnnotatorsWithRoles = annotatorRepository.findAll();
        System.out.println("\nTous les annotateurs avec leurs rôles :");
        for (Annotator a : allAnnotatorsWithRoles) {
            System.out.println("ID: " + a.getIdUser() +
                    " | Email: " + a.getEmail() +
                    " | Rôle: " + (a.getRole() != null ? a.getRole().getNomRole() : "null"));
        }

        // Garder uniquement ceux ayant le rôle ANNOTATOR_ROLE
        List<Annotator> annotatorsWithRole = allAnnotatorsWithRoles.stream()
                .filter(a -> a.getRole() != null && "ANNOTATOR_ROLE".equals(a.getRole().getNomRole()))
                .toList();

        System.out.println("\nAnnotateurs avec le rôle ANNOTATOR_ROLE (inclus même ceux déjà assignés) :");
        for (Annotator a : annotatorsWithRole) {
            System.out.println("ID: " + a.getIdUser() + " | Email: " + a.getEmail());
        }

        System.out.println("\n=== FIN DU DÉBOGAGE DES ANNOTATEURS ===\n");

        return annotatorsWithRole;
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

    @Override
    public int countActiveAnnotators() {
        return (int) annotatorRepository.count();
    }
} 