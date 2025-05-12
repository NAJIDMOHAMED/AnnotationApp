package ma.najid.annotationapp.service;

import org.springframework.web.multipart.MultipartFile;

public interface DatasetService {
    void saveDataset(MultipartFile file, String name, String classes, String description) throws Exception;
    // You can add more methods for listing, finding, or deleting datasets
} 