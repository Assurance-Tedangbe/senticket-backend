package sn.estm.managingrestauranttickets.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ IllegalArgumentException → 400 Bad Request
    // Évite que Spring Security transforme en 401
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(
            IllegalArgumentException e) {
        log.error("Erreur de validation: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Bad Request",
                        "message", e.getMessage(),
                        "status", "400"
                ));
    }

    // ✅ IllegalStateException → 422 Unprocessable Entity
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(
            IllegalStateException e) {
        log.error("Erreur d'état: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of(
                        "error", "Unprocessable Entity",
                        "message", e.getMessage(),
                        "status", "422"
                ));
    }

    // ✅ ResourceNotFoundException → 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(
            ResourceNotFoundException e) {
        log.error("Ressource non trouvée: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "Not Found",
                        "message", e.getMessage(),
                        "status", "404"
                ));
    }

    // ✅ RuntimeException générique → 500
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(
            RuntimeException e) {
        log.error("Erreur serveur: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Internal Server Error",
                        "message", "Une erreur inattendue s'est produite",
                        "status", "500"
                ));
    }
}