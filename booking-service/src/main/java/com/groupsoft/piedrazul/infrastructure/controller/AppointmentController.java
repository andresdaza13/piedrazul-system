package com.groupsoft.piedrazul.booking.infrastructure.controller;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO;
import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO;
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
}