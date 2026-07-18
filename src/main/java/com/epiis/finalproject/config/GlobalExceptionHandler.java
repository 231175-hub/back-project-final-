package com.epiis.finalproject.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new java.util.HashMap<>();
        java.util.List<String> listMessage = new java.util.ArrayList<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            listMessage.add(error.getDefaultMessage());
        });
        
        response.put("type", "error");
        response.put("listMessage", listMessage);
        
        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.BAD_REQUEST);
    }
}
