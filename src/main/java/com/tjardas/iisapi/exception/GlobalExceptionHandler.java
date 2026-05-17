package com.tjardas.iisapi.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tjardas.iisapi.dto.ErrorResponse;
import jakarta.xml.bind.JAXBException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(XmlValidationException.class)
    public ResponseEntity<ErrorResponse> handleXmlValidation(XmlValidationException ex) {
        Map<String, String> errors = ex.getErrors();
        if (errors != null && errors.containsKey("xml") && errors.size() == 1) {
            return ResponseEntity.badRequest().body(ErrorResponse.builder()
                    .message("XML validation failed.")
                    .detail(errors.get("xml"))
                    .build());
        }

        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .message("XML validation failed.")
                .errors(errors)
                .build());
    }

    @ExceptionHandler(MalformedXmlException.class)
    public ResponseEntity<ErrorResponse> handleMalformedXml(MalformedXmlException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .message("Malformed XML.")
                .detail(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage())
                .build());
    }

    @ExceptionHandler(SaveOperationException.class)
    public ResponseEntity<ErrorResponse> handleSaveError(SaveOperationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                .message("Validation succeeded, but saving failed.")
                .detail(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .message("Validation failed.")
                .errors(errors)
                .build());
    }

    @ExceptionHandler({SAXException.class, XMLStreamException.class, JAXBException.class})
    public ResponseEntity<ErrorResponse> handleXmlFrameworkExceptions(Exception ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .message("XML validation failed.")
                .detail(ex.getMessage())
                .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof InvalidFormatException ife) {
            String field = extractLastFieldName(ife);
            if (field != null) {
                return ResponseEntity.badRequest().body(ErrorResponse.builder()
                        .message("Invalid request data.")
                        .errors(Map.of(field, fieldTypeMessage(field)))
                        .build());
            }
        }

        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .message("Invalid request data.")
                .detail(cause != null ? cause.getMessage() : ex.getMessage())
                .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        String message = ex.getMessage() != null && ex.getMessage().contains("Supported schema types")
                ? "Unsupported validation schema."
                : "Invalid request.";
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .message(message)
                .detail(ex.getMessage())
                .build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                .message("Unexpected server error.")
                .detail(ex.getMessage())
                .build());
    }

    private String extractLastFieldName(InvalidFormatException exception) {
        if (exception.getPath() == null || exception.getPath().isEmpty()) {
            return null;
        }
        JsonMappingException.Reference ref = exception.getPath().get(exception.getPath().size() - 1);
        return ref.getFieldName();
    }

    private String fieldTypeMessage(String field) {
        String normalized = field.toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "points" -> "Points must be a valid number.";
            case "season" -> "Season must be a valid integer.";
            case "name" -> "Name is required.";
            case "team" -> "Team is required.";
            default -> "Invalid value for field '" + field + "'.";
        };
    }
}
