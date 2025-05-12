package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.service.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Controller
@RequestMapping("/dataset")
public class DatasetController {

    private final DatasetService datasetService;

    @Autowired
    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    // Show the dataset creation page
    @GetMapping("/create")
    public String showCreateDatasetPage(Model model) {
        return "dataset/create";
    }

    // Handle dataset creation (file upload, name, classes, description)
    @PostMapping("/api/dataset")
    @ResponseBody
    public ResponseEntity<?> createDataset(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("classes") String classes,
            @RequestParam("description") String description
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "File is required"));
        }
        try {
            datasetService.saveDataset(file, name, classes, description);
            return ResponseEntity.ok(Map.of("message", "Dataset uploaded successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
} 