package cloud.praetoria.auth.advice;

import cloud.praetoria.auth.dtos.responses.ErrorResponse;
import cloud.praetoria.auth.exceptions.ApiException;
import cloud.praetoria.auth.exceptions.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorCode ec = ex.getErrorCode();
        ErrorResponse body = new ErrorResponse(
                ec.getStatus(),
                ec.getCode(),
                ec.getMessage(),
                ex.getDetails()
        );
        return ResponseEntity.status(ec.getStatus()).body(body);
    }

    // Validation @Valid on request body -> MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.BAD_REQUEST.getCode(),
                "Données invalides",
                details
        );
        return ResponseEntity.badRequest().body(body);
    }

    // Validation on params (@Validated) -> ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String details = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.BAD_REQUEST.getCode(),
                "Paramètres invalides",
                details
        );
        return ResponseEntity.badRequest().body(body);
    }

    // JSON mal formé
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        ex.getMostSpecificCause();
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.BAD_REQUEST.getCode(),
                "JSON mal formé",
                ex.getMostSpecificCause().getMessage()
        );
        return ResponseEntity.badRequest().body(body);
    }

    // Accès refusé
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "ACCESS_DENIED",
                "Accès refusé",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // Catch-all : autres exceptions -> ne pas exposer le message interne
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        // log l'exception ici (logger.error(..., ex))
        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ErrorCode.INTERNAL_ERROR.getCode(),
                ErrorCode.INTERNAL_ERROR.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
