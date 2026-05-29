package com.groupsoft.piedrazul.user.infrastructure.controller;

import com.groupsoft.piedrazul.user.application.dto.UserResponseDTO;
import com.groupsoft.piedrazul.user.application.dto.UserWhatsAppDTO;
import com.groupsoft.piedrazul.user.application.service.UserService;
import com.groupsoft.piedrazul.user.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestion de usuarios y pacientes")
public class UserController {

    private final UserService service;

    @PostMapping("/whatsapp-contact")
    @Operation(summary = "Registrar paciente desde WhatsApp",
               description = "Requisito 2: find-or-create y emite evento para agendamiento")
    public ResponseEntity<Map<String, Object>> registerFromWhatsApp(@Valid @RequestBody UserWhatsAppDTO request) {
        User user = service.registerOrGetUserFromWhatsApp(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of(
                        "message", "Paciente procesado exitosamente",
                        "patientId", user.getId()
                )
        );
    }

    @PostMapping("/web-register")
    @Operation(summary = "Registrar paciente desde portal web",
               description = "Requisito 3: registro de usuario paciente sin evento de WhatsApp")
    public ResponseEntity<Map<String, Object>> registerWebPatient(@Valid @RequestBody UserWhatsAppDTO request) {
        User user = service.registerWebPatient(request);
        return ResponseEntity.ok(Map.of(
                "message", "Paciente registrado en el portal web",
                "patientId", user.getId()
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar usuario por ID")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = service.getUserById(id);
        return ResponseEntity.ok(UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .documentNumber(user.getDocumentNumber())
                .phone(user.getPhone())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .email(user.getEmail())
                .role(user.getRole())
                .build());
    }
}
