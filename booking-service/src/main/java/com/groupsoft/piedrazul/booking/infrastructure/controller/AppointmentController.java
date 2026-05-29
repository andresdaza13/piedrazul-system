package com.groupsoft.piedrazul.booking.infrastructure.controller;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO;
import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO;
import com.groupsoft.piedrazul.booking.application.dto.RescheduleRequestDTO;
import com.groupsoft.piedrazul.booking.application.dto.WebBookingRequestDTO;
import com.groupsoft.piedrazul.booking.application.facade.WebBookingFacade;
import com.groupsoft.piedrazul.booking.application.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Gestion de citas medicas - Piedrazul")
public class AppointmentController {

    private final AppointmentService service;
    private final WebBookingFacade webBookingFacade;

    @PostMapping
    @Operation(summary = "Crear nueva cita",
               description = "Requisito 2: Crea una cita para un paciente contactado por WhatsApp")
    public ResponseEntity<AppointmentResponseDTO> create(
            @Valid @RequestBody AppointmentRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createAppointment(request));
    }

    @PostMapping("/web-booking")
    @Operation(summary = "Agendar cita web (Paciente)",
               description = "Requisito 3: Orquesta verificacion de disponibilidad y creacion de cita web")
    public ResponseEntity<Map<String, String>> createWebBooking(
            @RequestBody WebBookingRequestDTO request) {
        try {
            String resultMessage = webBookingFacade.processWebBooking(request);
            return ResponseEntity.ok(Map.of("message", resultMessage));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Listar citas por medico y fecha",
               description = "Requisito 1: Retorna citas de un medico en una fecha con el total")
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

    @GetMapping("/doctor/{doctorId}/occupied-times")
    @Operation(summary = "Horarios ocupados del medico",
               description = "Usado por availability-service para filtrar franjas libres")
    public ResponseEntity<List<LocalTime>> getOccupiedTimes(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(service.getOccupiedTimes(doctorId, date));
    }

    @GetMapping("/doctor/{doctorId}/export")
    @Operation(summary = "Exportar citas a CSV",
               description = "Requisito 5: Exporta citas de un medico en una fecha para hoja de calculo")
    public ResponseEntity<String> exportToCsv(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        String csv = service.exportAppointmentsToCsv(doctorId, date);
        String filename = "citas_medico_" + doctorId + "_" + date + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @PutMapping("/{appointmentId}/reschedule")
    @Operation(summary = "Re-agendar cita",
               description = "Requisito 6: Cambia fecha/hora conservando historial y responsable")
    public ResponseEntity<AppointmentResponseDTO> reschedule(
            @PathVariable Long appointmentId,
            @Valid @RequestBody RescheduleRequestDTO request) {
        return ResponseEntity.ok(service.rescheduleAppointment(appointmentId, request));
    }

    @GetMapping("/{appointmentId}/reschedule-history")
    @Operation(summary = "Historial de re-agendamientos",
               description = "Requisito 6: Consulta cambios previos de una cita")
    public ResponseEntity<?> getRescheduleHistory(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(service.getRescheduleHistory(appointmentId));
    }
}
