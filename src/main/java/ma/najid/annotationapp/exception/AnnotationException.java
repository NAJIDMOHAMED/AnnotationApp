package ma.najid.annotationapp.exception;

public class AnnotationException extends RuntimeException {
    
    public AnnotationException(String message) {
        super(message);
    }
    
    public AnnotationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static class UnauthorizedAccessException extends AnnotationException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }
    
    public static class DuplicateAnnotationException extends AnnotationException {
        public DuplicateAnnotationException(String message) {
            super(message);
        }
    }

    public static class ResourceNotFoundException extends AnnotationException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
} 