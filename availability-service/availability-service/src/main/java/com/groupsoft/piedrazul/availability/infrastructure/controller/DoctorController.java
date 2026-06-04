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

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Gestion de medicos y terapistas")
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @Operation(summary = "Crear medico o terapista")
    public ResponseEntity<DoctorResponseDTO> createDoctor(
            @RequestBody DoctorRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(doctorService.createDoctor(request));
    }

    @GetMapping
    @Operation(summary = "Listar todos los medicos")
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar medico por ID")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }
}