package com.groupsoft.piedrazul.availability.infrastructure.controller;

import com.groupsoft.piedrazul.availability.application.dto.AvailabilityRequestDTO;
import com.groupsoft.piedrazul.availability.application.dto.AvailabilityResponseDTO;
import com.groupsoft.piedrazul.availability.application.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
@Tag(name = "Availability", description = "Configuracion de disponibilidad de medicos")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping("/configure")
    @Operation(summary = "Configurar disponibilidad del medico",
               description = "Requisito 4: Administrador configura dias, horarios e intervalos")
    public ResponseEntity<AvailabilityResponseDTO> configureDoctorAvailability(
            @RequestBody AvailabilityRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(availabilityService.configureAvailability(request));
    }

    @GetMapping("/slots")
    @Operation(summary = "Ver franjas disponibles",
               description = "Requisito 3: Retorna las franjas horarias disponibles de un medico en una fecha")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        return ResponseEntity.ok(availabilityService.getAvailableSlots(doctorId, targetDate));
    }
}