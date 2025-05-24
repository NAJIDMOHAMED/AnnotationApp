package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.Administrator;
import ma.najid.annotationapp.Model.UserAccount;
import ma.najid.annotationapp.service.*;
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
public class AdministratorController {

    private final AdministratorService administratorService;
    private  final UserAccountService userAccountService;
    private final AnnotatorService annotatorService;
    private  final DatasetService datasetService;
    private final TacheService tacheService;

    @Autowired
    public AdministratorController(AdministratorService administratorService, UserAccountService userAccountService, AnnotatorService annotatorService, DatasetService datasetService, TacheService tacheService) {
        this.administratorService = administratorService;
        this.userAccountService = userAccountService;
        this.annotatorService = annotatorService;
        this.datasetService = datasetService;
        this.tacheService = tacheService;
    }

    @GetMapping("/administrator")
    public String getAdministrator(Model model) {
        userAccountService.getAdmin()
            .ifPresent(admin -> model.addAttribute("administrator", admin));
        return "administrator/view";
    }



    @PostMapping("/api/administrator")
    @ResponseBody
    public ResponseEntity<?> showAdmin() {
        try {
            List<UserAccount> admins = userAccountService.getAdmins();
            if (admins.isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "No admin found"), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(admins.get(0), HttpStatus.OK); // si tu as un seul admin
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/api/administrator")
    @ResponseBody
    public ResponseEntity<Administrator> getAdministrator() {
        return administratorService.getAdministratorById(1L)
                .map(administrator -> new ResponseEntity<>(administrator, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/api/administrator")
    @ResponseBody
    public ResponseEntity<?> updateAdministrator(@Valid @RequestBody Administrator administratorDetails, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            Administrator updatedAdministrator = administratorService.updateAdministrator(1L, administratorDetails);
            return new ResponseEntity<>(updatedAdministrator, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/admin/adminHome")
    public String showAdminHome(Model model) {
        int activeAnnotators = annotatorService.countActiveAnnotators();
        int totalDatasets = datasetService.countDatasets();
        int completedTasks = tacheService.countCompletedTasks();
        int averageProgress = tacheService.getAverageProgress();
        model.addAttribute("activeAnnotators", activeAnnotators);
        model.addAttribute("totalDatasets", totalDatasets);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("averageProgress", averageProgress);
        return "administrator/adminHome";
    }
} 