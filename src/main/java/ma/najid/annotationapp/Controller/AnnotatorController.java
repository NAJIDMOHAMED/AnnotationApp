package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.service.AnnotatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AnnotatorController {

    private final AnnotatorService annotatorService;

    @Autowired
    public AnnotatorController(AnnotatorService annotatorService) {
        this.annotatorService = annotatorService;
    }

    // Web interface endpoint
    @GetMapping("/annotators")
    public String listAnnotators(Model model) {
        List<Annotator> annotators = annotatorService.getAllAnnotators();
        model.addAttribute("annotators", annotators);
        return "annotator/list";
    }

    // REST API endpoints
    @PostMapping("/api/annotators")
    @ResponseBody
    public ResponseEntity<?> createAnnotator(@Valid @RequestBody Annotator annotator, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        if (annotatorService.existsByEmail(annotator.getEmail())) {
            return new ResponseEntity<>(Map.of("email", "Email already exists"), HttpStatus.CONFLICT);
        }

        try {
            Annotator savedAnnotator = annotatorService.saveAnnotator(annotator);
            return new ResponseEntity<>(savedAnnotator, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/annotators")
    @ResponseBody
    public ResponseEntity<List<Annotator>> getAllAnnotators() {
        List<Annotator> annotators = annotatorService.getAllAnnotators();
        return new ResponseEntity<>(annotators, HttpStatus.OK);
    }

    @GetMapping("/api/annotators/{id}")
    @ResponseBody
    public ResponseEntity<Annotator> getAnnotatorById(@PathVariable Long id) {
        return annotatorService.getAnnotatorById(id)
                .map(annotator -> new ResponseEntity<>(annotator, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/api/annotators/email/{email}")
    @ResponseBody
    public ResponseEntity<Annotator> getAnnotatorByEmail(@PathVariable String email) {
        return annotatorService.getAnnotatorByEmail(email)
                .map(annotator -> new ResponseEntity<>(annotator, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/api/annotators/{id}")
    @ResponseBody
    public ResponseEntity<?> updateAnnotator(@PathVariable Long id, @Valid @RequestBody Annotator annotatorDetails, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            Annotator updatedAnnotator = annotatorService.updateAnnotator(id, annotatorDetails);
            return new ResponseEntity<>(updatedAnnotator, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/api/annotators/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAnnotator(@PathVariable Long id) {
        try {
            annotatorService.deleteAnnotator(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
} 