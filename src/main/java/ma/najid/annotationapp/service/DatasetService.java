package ma.najid.annotationapp.service;

import ma.najid.annotationapp.Model.Dataset;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DatasetService {
    void saveDataset(MultipartFile file, String name, String classes, String description) throws Exception;
    // You can add more methods for listing, finding, or deleting datasets

    void processExcelFile(MultipartFile file) throws IOException;

    List<Dataset> findAll();

    Dataset getDatasetById(Long id);

    List<Map<String, String>> getAnnotatedPairs(Long datasetId);

    int getRemainingPairs(Long datasetId);
}