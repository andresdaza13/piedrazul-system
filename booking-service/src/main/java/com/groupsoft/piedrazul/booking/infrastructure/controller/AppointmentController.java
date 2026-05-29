package com.groupsoft.piedrazul.booking.infrastructure.controller;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO;
import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO;
import com.groupsoft.piedrazul.booking.application.dto.WebBookingRequestDTO;
import com.groupsoft.piedrazul.booking.application.facade.WebBookingFacade;
import com.groupsoft.piedrazul.booking.application.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Gestion de citas medicas - Piedrazul")
public class AppointmentController {

    private final AppointmentService service;
    
    // Inyección de nuestra Fachada (Patrón Estructural)
    private final WebBookingFacade webBookingFacade;

    // Requisito 2 - Crear cita desde WhatsApp
    @PostMapping
    @Operation(summary = "Crear nueva cita",
               description = "Crea una cita para un paciente contactado por WhatsApp")
    public ResponseEntity<AppointmentResponseDTO> create(
            @RequestBody AppointmentRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createAppointment(request));
    }

    // Requisito 3 - Crear cita desde Web (Integración Patrón Facade y Adapter)
    @PostMapping("/web-booking")
    @Operation(summary = "Agendar cita web (Paciente)",
               description = "Orquesta la verificación de disponibilidad y creación de la cita web usando el patrón Facade")
    public ResponseEntity<Map<String, String>> createWebBooking(
            @RequestBody WebBookingRequestDTO request) {
        try {
            String resultMessage = webBookingFacade.processWebBooking(request);
            return ResponseEntity.ok(Map.of("message", resultMessage));
        } catch (RuntimeException e) {
            // Manejo de la excepción si la hora ya no está disponible
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Requisito 1 - Listar citas de un medico en una fecha
    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Listar citas por medico y fecha",
               description = "Retorna todas las citas de un medico en una fecha especifica con el total")
    public ResponseEntity<Map<String, Object>> getByDoctorAndDate(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<AppointmentResponseDTO> appointments =
                service.getAppointmentsByDoctorAndDate(doctorId, date);

        return ResponseEntity.ok(Map.of(
                "doctorId", doctorId,
                "date", date,
                "total", appointments.size(),
                "appointments", appointments
        ));

            
    } 

    // ===== STATE PATTERN — Transiciones de estado =====

@PatchMapping("/{id}/confirm")
@Operation(summary = "Confirmar cita",
           description = "Transicion: PENDING -> CONFIRMED")
public ResponseEntity<AppointmentResponseDTO> confirm(@PathVariable Long id) {
    return ResponseEntity.ok(service.confirmAppointment(id));
}

@PatchMapping("/{id}/start")
@Operation(summary = "Iniciar consulta",
           description = "Transicion: CONFIRMED -> IN_PROGRESS")
public ResponseEntity<AppointmentResponseDTO> start(@PathVariable Long id) {
    return ResponseEntity.ok(service.startAppointment(id));
}

@PatchMapping("/{id}/complete")
@Operation(summary = "Completar cita",
           description = "Transicion: IN_PROGRESS -> COMPLETED")
public ResponseEntity<AppointmentResponseDTO> complete(@PathVariable Long id) {
    return ResponseEntity.ok(service.completeAppointment(id));
}

@PatchMapping("/{id}/cancel")
@Operation(summary = "Cancelar cita",
           description = "Transicion: PENDING/CONFIRMED/IN_PROGRESS -> CANCELLED")
public ResponseEntity<AppointmentResponseDTO> cancel(@PathVariable Long id) {
    return ResponseEntity.ok(service.cancelAppointment(id));
}

@PatchMapping("/{id}/no-show")
@Operation(summary = "Marcar paciente no asistio",
           description = "Transicion: CONFIRMED -> NO_SHOW")
public ResponseEntity<AppointmentResponseDTO> noShow(@PathVariable Long id) {
    return ResponseEntity.ok(service.markNoShow(id));
}
}