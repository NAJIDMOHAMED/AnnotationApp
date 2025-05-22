package ma.najid.annotationapp.service.impl;

import ma.najid.annotationapp.Model.*;
import ma.najid.annotationapp.repository.DatasetRepository;
import ma.najid.annotationapp.repository.TextPairRepository;
import ma.najid.annotationapp.repository.PossibleClassesRepository;
import ma.najid.annotationapp.repository.TacheRepository;
import ma.najid.annotationapp.repository.AnnotatorRepository;
import ma.najid.annotationapp.service.DatasetService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Date;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

@Service
public class DatasetServiceImpl implements DatasetService {
    private static final Logger logger = LoggerFactory.getLogger(DatasetServiceImpl.class);

    @Autowired
    private TextPairRepository textPairRepository;

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private PossibleClassesRepository possibleClassesRepository;

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private AnnotatorRepository annotatorRepository;

    @Override
    @Transactional
    public void saveDataset(MultipartFile file, String name, String classes, String description) throws Exception {
        logger.info("Starting dataset save process: name={}, classes={}, description={}", name, classes, description);
        
        // Create and save dataset entity
        Dataset dataset = new Dataset();
        dataset.setNom(name);
        dataset.setDescription(description);
        
        // Process and save classes
        if (classes != null && !classes.trim().isEmpty()) {
            List<PossibleClasses> possibleClassesList = new ArrayList<>();
            String[] classArray = classes.split("[,;]");
            if (classArray.length > 2) {
                throw new IllegalArgumentException("Le nombre de classes séparées par ';' ne doit pas dépasser 2");
            }
            
            for (String className : classArray) {
                String trimmedClass = className.trim();
                if (!trimmedClass.isEmpty()) {
                    PossibleClasses possibleClass = new PossibleClasses();
                    possibleClass.setTypeClass(trimmedClass);
                    possibleClass.setDataset(dataset);
                    possibleClassesList.add(possibleClass);
                    logger.info("Adding class: {}", trimmedClass);
                }
            }
            
            // Save dataset first to get the ID
            dataset = datasetRepository.save(dataset);
            logger.info("Dataset saved with ID: {}", dataset.getIdDataset());
            
            // Save all possible classes
            possibleClassesRepository.saveAll(possibleClassesList);
            logger.info("Saved {} possible classes", possibleClassesList.size());
        } else {
            dataset = datasetRepository.save(dataset);
            logger.info("Dataset saved with ID: {} (no classes)", dataset.getIdDataset());
        }
        
        // Process Excel file and save TextPairs
        if (file != null && !file.isEmpty()) {
            try {
                Workbook workbook = new XSSFWorkbook(file.getInputStream());
                Sheet sheet = workbook.getSheetAt(0);
                List<TextPair> textPairs = new ArrayList<>();

                Row headerRow = sheet.getRow(0);
                if (headerRow == null || headerRow.getPhysicalNumberOfCells() < 2) {
                    workbook.close();
                    throw new IllegalArgumentException("Le fichier Excel doit contenir au moins deux colonnes");
                }

                int sentence1Index = -1;
                int sentence2Index = -1;
                int found = 0;

                for (Cell cell : headerRow) {
                    if (found == 0) {
                        sentence1Index = cell.getColumnIndex();
                        found++;
                    } else if (found == 1) {
                        sentence2Index = cell.getColumnIndex();
                        break;
                    }
                }

                if (sentence1Index == -1 || sentence2Index == -1) {
                    workbook.close();
                    throw new IllegalArgumentException("Impossible d'identifier deux colonnes de texte dans l'en-tête");
                }

                // Read data rows
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    Cell cell1 = row.getCell(sentence1Index);
                    Cell cell2 = row.getCell(sentence2Index);

                    if (cell1 != null && cell2 != null) {
                        String text1 = getCellValueAsString(cell1);
                        String text2 = getCellValueAsString(cell2);
                        
                        if (!text1.trim().isEmpty() && !text2.trim().isEmpty()) {
                            TextPair textPair = new TextPair();
                            textPair.setText1(text1);
                            textPair.setText2(text2);
                            textPair.setDataset(dataset);
                            textPairs.add(textPair);
                            logger.info("Adding text pair: {} - {}", text1, text2);
                        }
                    }
                }

                // Save TextPairs
                if (!textPairs.isEmpty()) {
                    List<TextPair> savedPairs = textPairRepository.saveAll(textPairs);
                    logger.info("Successfully saved {} text pairs to database for dataset ID: {}", 
                              savedPairs.size(), dataset.getIdDataset());
                    
                    // Create tasks for all annotators
                    createTasksForAllAnnotators(dataset, savedPairs);
                } else {
                    logger.warn("No valid text pairs found in the Excel file");
                }

                workbook.close();
            } catch (Exception e) {
                logger.error("Error processing Excel file: {}", e.getMessage(), e);
                throw new Exception("Error processing Excel file: " + e.getMessage());
            }
        }

        logger.info("Dataset save process completed successfully");
    }

    private void createTasksForAllAnnotators(Dataset dataset, List<TextPair> textPairs) {
        List<ma.najid.annotationapp.Model.Annotator> annotators = annotatorRepository.findAll();
        if (annotators.isEmpty()) {
            logger.warn("Aucun annotateur trouvé, aucune tâche créée.");
            return;
        }
        int totalPairs = textPairs.size();
        int annotatorCount = annotators.size();
//        int pairsPerAnnotator = totalPairs / annotatorCount;
        int pairsPerAnnotator = 20;
        int remaining = totalPairs % annotatorCount;
        int currentIndex = 0;
        for (Annotator annotator : annotators) {
            int count = pairsPerAnnotator + (remaining > 0 ? 1 : 0);
            if (remaining > 0) remaining--;
            List<TextPair> subList = textPairs.subList(currentIndex, Math.min(currentIndex + count, totalPairs));
            currentIndex += count;
            Tache tache = new Tache();
            tache.setAnnotator(annotator);
            tache.setDataset(dataset);
            tache.setDateLimite(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));
            tache.setTextPairs(new HashSet<>(subList));
            for (TextPair tp : subList) {
                tp.setTache(tache);
                tp.setDataset(dataset);
            }
            tacheRepository.save(tache);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    public void processExcelFile(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        List<TextPair> textPairs = new ArrayList<>();

        Row headerRow = sheet.getRow(0);
        int sentence1Index = -1;
        int sentence2Index = -1;

        // Trouver les index des colonnes "sentence1" et "sentence2"
        for (Cell cell : headerRow) {
            String header = cell.getStringCellValue().trim().toLowerCase();
            if (header.equals("sentence1")) {
                sentence1Index = cell.getColumnIndex();
            } else if (header.equals("sentence2")) {
                sentence2Index = cell.getColumnIndex();
            }
        }

        if (sentence1Index == -1 || sentence2Index == -1) {
            workbook.close();
            throw new IllegalArgumentException("Le fichier Excel ne contient pas les colonnes 'sentence1' et 'sentence2'");
        }

        // Lire les lignes de données
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Cell cell1 = row.getCell(sentence1Index);
            Cell cell2 = row.getCell(sentence2Index);

            if (cell1 != null && cell2 != null) {
                TextPair textPair = new TextPair();
                textPair.setText1(getCellValueAsString(cell1));
                textPair.setText2(getCellValueAsString(cell2));
                textPairs.add(textPair);
            }
        }

        // Sauvegarde en base
        if (!textPairs.isEmpty()) {
            textPairRepository.saveAll(textPairs);
        }

        workbook.close();
    }

    @Override
    public List<Dataset> findAll() {

        return  datasetRepository.findAll();
    }

    @Override
    public Dataset getDatasetById(Long id) {
        return datasetRepository.findById(id).orElse(null);
    }

    @Override
    public List<Map<String, String>> getAnnotatedPairs(Long datasetId) {
        List<TextPair> textPairs = textPairRepository.findByDataset_IdDataset(datasetId);
        List<Map<String, String>> annotatedPairs = new ArrayList<>();

        for (TextPair pair : textPairs) {
            if (pair.isAnnotated()) {
                Map<String, String> pairData = new HashMap<>();
                pairData.put("id", pair.getIdTextPair().toString());
                pairData.put("text1", pair.getText1());
                pairData.put("text2", pair.getText2());
                
                // Get the latest annotation
                String annotation = pair.getAnnotations().stream()
                    .max(Comparator.comparing(Annotation::getDateAnnotation))
                    .map(a -> a.getPossibleClass().getTypeClass())
                    .orElse("");
                
                pairData.put("annotation", annotation);
                annotatedPairs.add(pairData);
            }
        }

        return annotatedPairs;
    }

    @Override
    public int getRemainingPairs(Long datasetId) {
        return textPairRepository.findByDataset_IdDatasetAndTacheIsNull(datasetId).size();
    }

} 