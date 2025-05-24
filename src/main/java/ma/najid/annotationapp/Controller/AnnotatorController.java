package ma.najid.annotationapp.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.service.AnnotatorService;
import ma.najid.annotationapp.service.Email.EmailService;
import ma.najid.annotationapp.service.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/annotator")
public class AnnotatorController {
    private final AnnotatorService annotatorService;
    private final TacheService tacheService;
    private EmailService emailService;

    @Autowired
    public AnnotatorController(AnnotatorService annotatorService, TacheService tacheService, EmailService emailService) {
        this.annotatorService = annotatorService;
        this.tacheService = tacheService;
        this.emailService = emailService;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        Hibernate6Module hibernateModule = new Hibernate6Module();
        hibernateModule.configure(Hibernate6Module.Feature.FORCE_LAZY_LOADING, false);
        mapper.registerModule(hibernateModule);
        return mapper;
    }


    @GetMapping("/list")
    public String listAnnotators(Model model) {
        List<Annotator> annotators = annotatorService.getAllAnnotators();
        model.addAttribute("annotators", annotators);
        model.addAttribute("generatedPassword", generateRandomPassword(10));
        return "annotator/list";
    }

    @GetMapping("/api")
    @ResponseBody
    public List<Annotator> getAllAnnotators() {
        return annotatorService.getAllAnnotators();
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> createAnnotator(@Valid @RequestBody Annotator annotator, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        
        String generatedPassword = generateRandomPassword(14);
        
        annotator.setPassword(generatedPassword);

        
        Annotator savedAnnotator = annotatorService.saveAnnotator(annotator);
        
        try {
            emailService.sendPasswordEmail(annotator.getEmail(), annotator.getNom(), generatedPassword);
            return ResponseEntity.ok(savedAnnotator);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("annotator", savedAnnotator);
            response.put("warning", "L'annotateur a été créé mais l'envoi de l'email a échoué. Veuillez communiquer le mot de passe manuellement.");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> getAnnotator(@PathVariable Long id) {
        try {
            Annotator annotator = annotatorService.findById(id);
            return ResponseEntity.ok(annotator);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Annotator not found with id: " + id));
        }
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> updateAnnotator(@PathVariable Long id, @Valid @RequestBody Annotator annotator, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        if (!annotatorService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        annotator.setIdUser(id);
        Annotator updatedAnnotator = annotatorService.saveAnnotator(annotator);
        return ResponseEntity.ok(updatedAnnotator);
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAnnotator(@PathVariable Long id) {
        if (!annotatorService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        annotatorService.deleteAnnotator(id);
        return ResponseEntity.noContent().build();
    }

    private String generateRandomPassword(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("La longueur minimale doit être d'au moins 4 pour garantir un mot de passe fort.");
        }

        String lower = "abcdefghijklmnopqrstuvwxyz";
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String special = "#$@";
        String all = lower + upper + digits + special;

        StringBuilder sb = new StringBuilder();

        java.util.Random random = new java.util.Random();

        // Ajouter un caractère de chaque type
        sb.append(lower.charAt(random.nextInt(lower.length())));
        sb.append(upper.charAt(random.nextInt(upper.length())));
        sb.append(digits.charAt(random.nextInt(digits.length())));
        sb.append(special.charAt(random.nextInt(special.length())));

        // Remplir le reste avec des caractères aléatoires
        for (int i = 4; i < length; i++) {
            sb.append(all.charAt(random.nextInt(all.length())));
        }

        // Mélanger les caractères pour ne pas avoir les 4 premiers dans le même ordre
        List<Character> passwordChars = new ArrayList<>();
        for (char c : sb.toString().toCharArray()) {
            passwordChars.add(c);
        }
        java.util.Collections.shuffle(passwordChars);

        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

} 