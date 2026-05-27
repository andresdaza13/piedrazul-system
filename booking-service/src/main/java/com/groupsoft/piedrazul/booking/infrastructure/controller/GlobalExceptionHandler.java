
package com.groupsoft.piedrazul.booking.infrastructure.controller;

import com.groupsoft.piedrazul.booking.domain.exception.AppointmentOverlapException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppointmentOverlapException.class)
    public ResponseEntity<Map<String, String>> handleOverlapException(AppointmentOverlapException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Conflicto de Horario");
        response.put("message", ex.getMessage());
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT); // Devuelve HTTP 409 Conflict
    }
}