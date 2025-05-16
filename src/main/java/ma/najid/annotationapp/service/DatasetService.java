package ma.najid.annotationapp.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DatasetService {
    void saveDataset(MultipartFile file, String name, String classes, String description) throws Exception;
    // You can add more methods for listing, finding, or deleting datasets

    void processExcelFile(MultipartFile file) throws IOException;
} 