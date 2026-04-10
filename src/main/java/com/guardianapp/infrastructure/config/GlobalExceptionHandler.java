package com.guardianapp.infrastructure.config;

import com.guardianapp.domain.exception.AlertException;
import com.guardianapp.domain.exception.DomainException;
import com.guardianapp.domain.exception.InvitationException;
import com.guardianapp.domain.exception.UserException;
import com.guardianapp.domain.exception.LinkException;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.ErrorResponse;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.ErrorResponse.FieldError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Global exception handler for the REST API.
 * Converts domain exceptions into appropriate HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles field validation exceptions.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .toList();

        ErrorResponse response = new ErrorResponse(
            "VALIDATION_ERROR",
            "Validation error in the submitted data",
            fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles user exceptions (not found, duplicate, etc.).
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException ex) {
        log.warn("User error: {}", ex.getMessage());
        
        ErrorResponse response = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        
        // Determine HTTP code based on message
        HttpStatus status = ex.getMessage().contains("not found") 
            ? HttpStatus.NOT_FOUND 
            : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Handles link exceptions.
     */
    @ExceptionHandler(LinkException.class)
    public ResponseEntity<ErrorResponse> handleLinkException(LinkException ex) {
        log.warn("Link error: {}", ex.getMessage());
        
        ErrorResponse response = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        
        HttpStatus status = ex.getMessage().contains("not found") 
            ? HttpStatus.NOT_FOUND 
            : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Handles invitation exceptions.
     */
    @ExceptionHandler(InvitationException.class)
    public ResponseEntity<ErrorResponse> handleInvitationException(InvitationException ex) {
        log.warn("Invitation error: {}", ex.getMessage());
        
        ErrorResponse response = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        
        HttpStatus status;
        if (ex.getMessage().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex.getMessage().contains("expired") || ex.getMessage().contains("cancelled")) {
            status = HttpStatus.GONE;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Handles alert exceptions.
     */
    @ExceptionHandler(AlertException.class)
    public ResponseEntity<ErrorResponse> handleAlertException(AlertException ex) {
        log.warn("Alert error: {}", ex.getMessage());
        
        ErrorResponse response = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        
        HttpStatus status;
        if (ex.getMessage().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex.getMessage().contains("not authorized")) {
            status = HttpStatus.FORBIDDEN;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Handles generic domain exceptions.
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        log.warn("Domain error: {}", ex.getMessage());
        
        ErrorResponse response = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles illegal argument exceptions.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        
        ErrorResponse response = new ErrorResponse("INVALID_ARGUMENT", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles any other unexpected exception.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        
        ErrorResponse response = new ErrorResponse(
            "INTERNAL_ERROR",
            "An internal error occurred. Please try again later."
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
