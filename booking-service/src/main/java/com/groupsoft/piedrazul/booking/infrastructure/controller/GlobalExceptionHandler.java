package com.groupsoft.piedrazul.booking.infrastructure.controller;

import com.groupsoft.piedrazul.booking.domain.exception.AppointmentOverlapException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice: indica que esta clase aplica consejos globales a todos los controladores REST.
// Permite capturar excepciones en un solo lugar y devolver respuestas consistentes.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler: define un método que maneja una excepción específica.
    // En este caso, AppointmentOverlapException (cuando dos citas se solapan).
    @ExceptionHandler(AppointmentOverlapException.class)
    public ResponseEntity<Map<String, String>> handleOverlapException(AppointmentOverlapException ex) {
        // Se construye un mapa con información del error.
        Map<String, String> response = new HashMap<>();
        response.put("error", "Conflicto de Horario"); // Mensaje genérico
        response.put("message", ex.getMessage());      // Mensaje detallado de la excepción
        
        // Devuelve la respuesta con estado HTTP 409 Conflict.
        // Esto indica al cliente que la solicitud no pudo completarse por un conflicto en los datos.
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
