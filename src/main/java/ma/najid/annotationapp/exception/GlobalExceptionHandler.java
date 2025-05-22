package ma.najid.annotationapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import ma.najid.annotationapp.exception.*;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(AnnotationException.class)
    public RedirectView handleAnnotationException(AnnotationException ex, RedirectAttributes redirectAttributes) {
        logger.error("Annotation error: {}", ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return new RedirectView("/error");
    }
    
    @ExceptionHandler(AnnotationException.UnauthorizedAccessException.class)
    public RedirectView handleUnauthorizedAccessException(AnnotationException.UnauthorizedAccessException ex, 
                                                        RedirectAttributes redirectAttributes) {
        logger.warn("Unauthorized access: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", "You are not authorized to perform this action");
        return new RedirectView("/login");
    }
    
    @ExceptionHandler(AnnotationException.DuplicateAnnotationException.class)
    public RedirectView handleDuplicateAnnotationException(AnnotationException.DuplicateAnnotationException ex,
                                                         RedirectAttributes redirectAttributes) {
        logger.warn("Duplicate annotation attempt: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return new RedirectView("/annotator/home");
    }
    
    @ExceptionHandler(AnnotationException.ResourceNotFoundException.class)
    public RedirectView handleResourceNotFoundException(AnnotationException.ResourceNotFoundException ex,
                                                     RedirectAttributes redirectAttributes) {
        logger.error("Resource not found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", "The requested resource was not found");
        return new RedirectView("/error");
    }
    
    @ExceptionHandler(Exception.class)
    public RedirectView handleGenericException(Exception ex, RedirectAttributes redirectAttributes) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("error", "An unexpected error occurred");
        return new RedirectView("/error");
    }
} 