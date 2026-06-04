package com.groupsoft.piedrazul.booking.infrastructure.controller;

import com.groupsoft.piedrazul.booking.domain.exception.AppointmentOverlapException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppointmentOverlapException.class)
    public ResponseEntity<Map<String, String>> handleOverlapException(AppointmentOverlapException ex) {
        return error(HttpStatus.CONFLICT, "Conflicto de Horario", ex.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBusinessRule(RuntimeException ex) {
        return error(HttpStatus.BAD_REQUEST, "Regla de negocio", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause().getMessage();
        if (message != null && message.contains("appointments_status_check")) {
            return error(HttpStatus.CONFLICT, "Estado no permitido",
                    "Reinicie booking-service para aplicar el parche de estado RESCHEDULED, "
                            + "o ejecute db/patch-rescheduled-status.sql en booking_db.");
        }
        return error(HttpStatus.CONFLICT, "Integridad de datos", "No se pudo guardar la cita.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonParse(HttpMessageNotReadableException ex) {
        return error(HttpStatus.BAD_REQUEST, "Formato invalido",
                "La fecha/hora enviada no es valida. Use el formato yyyy-MM-ddTHH:mm:ss");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return error(HttpStatus.BAD_REQUEST, "Datos invalidos", details);
    }

    private ResponseEntity<Map<String, String>> error(
            HttpStatus status, String error, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }
}
