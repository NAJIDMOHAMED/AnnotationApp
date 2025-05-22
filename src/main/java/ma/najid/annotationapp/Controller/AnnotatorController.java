package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.service.AnnotatorService;
import ma.najid.annotationapp.service.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/annotators")
public class AnnotatorController {
    private final AnnotatorService annotatorService;
    private final TacheService tacheService;

    @Autowired
    public AnnotatorController(AnnotatorService annotatorService, TacheService tacheService) {
        this.annotatorService = annotatorService;
        this.tacheService = tacheService;
    }

    @GetMapping
    @ResponseBody
    public List<Annotator> getAllAnnotators() {
        return annotatorService.getAllAnnotators();
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createAnnotator(@Valid @RequestBody Annotator annotator, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        Annotator savedAnnotator = annotatorService.saveAnnotator(annotator);
        return ResponseEntity.ok(savedAnnotator);
    }

    @PutMapping("/{id}")
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

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAnnotator(@PathVariable Long id) {
        if (!annotatorService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        annotatorService.deleteAnnotator(id);
        return ResponseEntity.noContent().build();
    }
} 