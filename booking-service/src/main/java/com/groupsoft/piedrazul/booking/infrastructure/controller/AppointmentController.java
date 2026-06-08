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

// @RestController: indica que esta clase expone endpoints REST.
// @RequestMapping: define la ruta base "/api/v1/appointments".
// @RequiredArgsConstructor: genera automáticamente el constructor con los atributos finales.
// @Tag: documentación Swagger/OpenAPI para agrupar endpoints bajo "Appointments".
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Gestion de citas medicas - Piedrazul")
public class AppointmentController {

    // Servicio principal que contiene la lógica de negocio de citas.
    private final AppointmentService service;
    
    // Fachada (Patrón Facade): orquesta la lógica de agendamiento web,
    // integrando disponibilidad y creación de citas en una sola operación.
    private final WebBookingFacade webBookingFacade;

    // Requisito 2 - Crear cita desde WhatsApp
    // Endpoint POST /api/v1/appointments
    @PostMapping
    @Operation(summary = "Crear nueva cita",
               description = "Crea una cita para un paciente contactado por WhatsApp")
    public ResponseEntity<AppointmentResponseDTO> create(
            @RequestBody AppointmentRequestDTO request) {
        // Llama al servicio para crear la cita y retorna el resultado con código 201 CREATED.
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createAppointment(request));
    }

    // Requisito 3 - Agendamiento autónomo vía web
    // Endpoint POST /api/v1/appointments/web-booking
    @PostMapping("/web-booking")
    @Operation(summary = "Agendar cita web (Paciente)",
               description = "Orquesta la verificación de disponibilidad y creación de la cita web usando el patrón Facade")
    public ResponseEntity<Map<String, String>> createWebBooking(
            @RequestBody WebBookingRequestDTO request) {
        try {
            // Usa la fachada para procesar la reserva web.
            String resultMessage = webBookingFacade.processWebBooking(request);
            return ResponseEntity.ok(Map.of("message", resultMessage));
        } catch (RuntimeException e) {
            // Manejo de errores: si la franja ya no está disponible, retorna 400 BAD REQUEST.
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Requisito 1 - Listar citas de un médico en una fecha
    // Endpoint GET /api/v1/appointments/doctor/{doctorId}?date=YYYY-MM-DD
    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Listar citas por medico y fecha",
               description = "Retorna todas las citas de un medico en una fecha especifica con el total")
    public ResponseEntity<Map<String, Object>> getByDoctorAndDate(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Obtiene las citas desde el servicio.
        List<AppointmentResponseDTO> appointments =
                service.getAppointmentsByDoctorAndDate(doctorId, date);

        // Retorna un mapa con doctorId, fecha, total de citas y listado completo.
        return ResponseEntity.ok(Map.of(
                "doctorId", doctorId,
                "date", date,
                "total", appointments.size(),
                "appointments", appointments
        ));
    }
}
