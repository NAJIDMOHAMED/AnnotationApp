package ma.najid.annotationapp.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class AnnotationResponseDTO {
    private Long taskId;
    private Long textPairId;
    private String text1;
    private String text2;
    private int currentIndex;
    private int totalPairs;
    private int remainingPairs;
    private double progress;
    private boolean isAnnotated;
    private Long annotatorId;
} 