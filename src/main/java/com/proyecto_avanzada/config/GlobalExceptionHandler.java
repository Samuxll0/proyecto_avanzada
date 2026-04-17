package com.proyecto_avanzada.config;

import com.proyecto_avanzada.dto.GlobalDTOs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.access.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<GlobalDTOs.ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        GlobalDTOs.ErrorResponse error = new GlobalDTOs.ErrorResponse(
                ex.getStatusCode().value(),
                ex.getReason() != null ? ex.getReason() : "Error " + ex.getStatusCode().value(),
                System.currentTimeMillis());
        return new ResponseEntity<>(error, ex.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalDTOs.ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        GlobalDTOs.ErrorResponse errorResponse = new GlobalDTOs.ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Errores de validación: " + errors.toString(),
                System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GlobalDTOs.ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        GlobalDTOs.ErrorResponse error = new GlobalDTOs.ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Acceso denegado: No tiene permisos para realizar esta acción",
                System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalDTOs.ErrorResponse> handleAllOtherExceptions(Exception ex) {
        GlobalDTOs.ErrorResponse error = new GlobalDTOs.ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor: " + ex.getMessage(),
                System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
