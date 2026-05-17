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

import javax.xml.stream.XMLStreamException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(XmlValidationException.class)
    public ResponseEntity<ErrorResponse> handleXmlValidation(XmlValidationException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .message("XML validation failed.")
                .errors(ex.getErrors())
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
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> formatFieldError(fieldError))
                .toList();

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
        Throwable rootCause = ex.getMostSpecificCause();
        if (rootCause instanceof InvalidFormatException invalidFormatException) {
            return ResponseEntity.badRequest().body(ErrorResponse.builder()
                    .message("Validation failed.")
                    .errors(List.of(buildTypeErrorMessage(invalidFormatException)))
                    .build());
        }

        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .message("Validation failed.")
                .errors(List.of("Request payload is not readable."))
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

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private String buildTypeErrorMessage(InvalidFormatException ex) {
        String fieldName = ex.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .filter(name -> name != null && !name.isBlank())
                .findFirst()
                .orElse("field");

        return switch (fieldName) {
            case "season" -> "season must be a valid whole number.";
            case "points" -> "points must be a valid number.";
            default -> fieldName + " has an invalid value type.";
        };
    }
}
