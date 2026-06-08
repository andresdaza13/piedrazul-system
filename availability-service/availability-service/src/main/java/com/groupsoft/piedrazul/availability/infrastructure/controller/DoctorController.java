package com.groupsoft.piedrazul.availability.infrastructure.controller;

import com.groupsoft.piedrazul.availability.application.dto.DoctorRequestDTO;
import com.groupsoft.piedrazul.availability.application.dto.DoctorResponseDTO;
import com.groupsoft.piedrazul.availability.application.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// @RestController: indica que esta clase expone endpoints REST.
// @RequestMapping: define la ruta base del controlador (/api/v1/doctors).
// @Tag: documentación Swagger para agrupar endpoints relacionados con médicos.
@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Gestion de medicos y terapistas")
public class DoctorController {

    // Inyección del servicio DoctorService, que contiene la lógica de negocio.
    private final DoctorService doctorService;

    /**
     * Endpoint para crear un nuevo médico o terapista.
     * @param request DTO con los datos básicos del médico.
     * @return DTO con el médico creado.
     */
    @PostMapping
    @Operation(summary = "Crear medico o terapista")
    public ResponseEntity<DoctorResponseDTO> createDoctor(
            @RequestBody DoctorRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED) // Devuelve código 201 (CREATED).
                .body(doctorService.createDoctor(request));
    }

    /**
     * Endpoint para listar todos los médicos registrados.
     * @return Lista de médicos en formato DTO.
     */
    @GetMapping
    @Operation(summary = "Listar todos los medicos")
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    /**
     * Endpoint para consultar un médico específico por su ID.
     * @param id Identificador del médico.
     * @return DTO con la información del médico.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar medico por ID")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }
}
