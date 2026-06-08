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

// @RestController: indica que esta clase expone endpoints REST.
// @RequestMapping: define la ruta base del controlador (/api/v1/availability).
// @Tag: documentación Swagger para agrupar endpoints relacionados con disponibilidad.
@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
@Tag(name = "Availability", description = "Configuracion de disponibilidad de medicos")
public class AvailabilityController {

    // Inyección del servicio AvailabilityService, que contiene la lógica de negocio.
    private final AvailabilityService availabilityService;

    /**
     * Endpoint para configurar la disponibilidad de un médico.
     * Requisito funcional 4: El administrador define días, horarios e intervalos.
     * @param request DTO con los datos de configuración.
     * @return DTO con la disponibilidad registrada.
     */
    @PostMapping("/configure")
    @Operation(summary = "Configurar disponibilidad del medico",
               description = "Requisito 4: Administrador configura dias, horarios e intervalos")
    public ResponseEntity<AvailabilityResponseDTO> configureDoctorAvailability(
            @RequestBody AvailabilityRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED) // Devuelve código 201 (CREATED).
                .body(availabilityService.configureAvailability(request));
    }

    /**
     * Endpoint para consultar las franjas horarias disponibles de un médico en una fecha.
     * Requisito funcional 3: Retorna las franjas horarias disponibles.
     * @param doctorId Identificador del médico.
     * @param targetDate Fecha en formato ISO (yyyy-MM-dd).
     * @return Lista de horas disponibles.
     */
    @GetMapping("/slots")
    @Operation(summary = "Ver franjas disponibles",
               description = "Requisito 3: Retorna las franjas horarias disponibles de un medico en una fecha")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        return ResponseEntity.ok(availabilityService.getAvailableSlots(doctorId, targetDate));
    }
}
