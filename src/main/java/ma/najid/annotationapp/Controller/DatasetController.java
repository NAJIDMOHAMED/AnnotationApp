package ma.najid.annotationapp.Controller;

import ma.najid.annotationapp.Model.Dataset;
import ma.najid.annotationapp.repository.DatasetRepository;
import ma.najid.annotationapp.service.DatasetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dataset")
public class DatasetController {

    private static final Logger logger = LoggerFactory.getLogger(DatasetController.class);
    private final DatasetService datasetService;


    @Autowired
    public DatasetController(DatasetService datasetService, DatasetRepository datasetRepository) {
        this.datasetService = datasetService;
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
        List<Dataset> datasets = datasetService.findAll();
        model.addAttribute("datasets", datasets);
        return "dataset/list";
    }

    @GetMapping("/{id}")
    public String showDatasetDetails(@PathVariable Long id, Model model) {
        Dataset dataset = datasetService.getDatasetById(id);
        if (dataset == null) {
            return "redirect:/dataset/list";
        }

        // Get annotated pairs for statistics
        List<Map<String, String>> annotatedPairs = datasetService.getAnnotatedPairs(id);
        int totalPairs = dataset.getTextPairs() != null ? dataset.getTextPairs().size() : 0;
        int annotatedPairsCount = annotatedPairs.size();

        model.addAttribute("dataset", dataset);
        model.addAttribute("totalPairs", totalPairs);
        model.addAttribute("annotatedPairs", annotatedPairsCount);
        model.addAttribute("possibleClasses", dataset.getPossibleClassesSet());
        
        return "dataset/details";
    }

    @GetMapping("/{id}/export")
    @ResponseBody
    public ResponseEntity<Resource> exportDataset(@PathVariable Long id) {
        try {
            Dataset dataset = datasetService.getDatasetById(id);
            if (dataset == null) {
                return ResponseEntity.notFound().build();
            }

            // Create CSV content with headers
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("ID,Text1,Text2,Annotation\n");

            // Get annotated pairs from the dataset
            List<Map<String, String>> annotatedPairs = datasetService.getAnnotatedPairs(id);
            
            for (Map<String, String> pair : annotatedPairs) {
                csvContent.append(String.format("%s,%s,%s,%s\n",
                    pair.get("id"),
                    escapeCsvField(pair.get("text1")),
                    escapeCsvField(pair.get("text2")),
                    escapeCsvField(pair.get("annotation"))
                ));
            }

            String filename = "dataset_" + dataset.getNom() + "_export.csv";
            ByteArrayResource resource = new ByteArrayResource(csvContent.toString().getBytes(StandardCharsets.UTF_8));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error exporting dataset", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String escapeCsvField(String field) {
        if (field == null) return "";
        // Escape quotes and wrap in quotes if contains comma or newline
        if (field.contains(",") || field.contains("\n") || field.contains("\"")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

} 