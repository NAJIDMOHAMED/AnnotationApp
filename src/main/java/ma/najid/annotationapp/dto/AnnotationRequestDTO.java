package ma.najid.annotationapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnnotationRequestDTO {
    @NotNull(message = "L'ID de la paire de texte est requis")
    private Long textPairId;
    
    @NotNull(message = "L'ID de la classe est requis")
    private Long possibleClassId;
    
    @NotNull(message = "L'ID de l'annotateur est requis")
    private Long annotatorId;
    
    private int index;
} 