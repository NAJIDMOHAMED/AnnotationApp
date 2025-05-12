package ma.najid.annotationapp.service.impl;

import ma.najid.annotationapp.service.DatasetService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DatasetServiceImpl implements DatasetService {
    private static final String UPLOAD_DIR = "uploads";

    @Override
    public void saveDataset(MultipartFile file, String name, String classes, String description) throws Exception {
        // Ensure upload directory exists
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        // Save file to local directory
        Path filePath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
        Files.write(filePath, file.getBytes());

        // Log or print metadata (replace with DB save as needed)
        System.out.println("Dataset saved:");
        System.out.println("  Name: " + name);
        System.out.println("  Classes: " + classes);
        System.out.println("  Description: " + description);
        System.out.println("  File: " + filePath.toAbsolutePath());
    }
} 