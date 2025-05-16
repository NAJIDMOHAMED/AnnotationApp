package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.Dataset;
import ma.najid.annotationapp.repository.DatasetRepository;
import ma.najid.annotationapp.service.DatasetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dataset")
public class DatasetController {

    private static final Logger logger = LoggerFactory.getLogger(DatasetController.class);

    private final DatasetService datasetService;
    private final DatasetRepository datasetRepository;

    @Autowired
    public DatasetController(DatasetService datasetService, DatasetRepository datasetRepository) {
        this.datasetService = datasetService;
        this.datasetRepository = datasetRepository;
    }

    // Show the dataset creation page
    @GetMapping("/create")
    public String showCreateDatasetPage(Model model) {
        return "dataset/create";
    }

    // Handle dataset creation (file upload, name, classes, description)
    @PostMapping("/api/dataset")
    public String createDataset(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("classes") String classes,
            @RequestParam("description") String description,
            Model model
    ) {
        logger.info("Received dataset creation request: name={}, classes={}, description={}", name, classes, description);
        if (file.isEmpty()) {
            logger.warn("File is empty");
            model.addAttribute("error", "File is required");
            return "dataset/create";
        }
        try {
            datasetService.saveDataset(file, name, classes, description);
            logger.info("Dataset saved successfully");
            return "redirect:/dataset/list"; // Redirect to the dataset list page
        } catch (Exception e) {
            logger.error("Error saving dataset", e);
            model.addAttribute("error", e.getMessage());
            return "dataset/create";
        }
    }

    @PostMapping("/upload")
    public String uploadExcelFile(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "File is required");
            return "dataset/list";
        }
        try {
            datasetService.processExcelFile(file);
            model.addAttribute("success", "File uploaded and processed successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "dataset/list";
    }

    @GetMapping("/list")
    public String listDatasets(Model model) {
        List<Dataset> datasets = datasetRepository.findAll();
        model.addAttribute("datasets", datasets);
        return "dataset/list";
    }
} 