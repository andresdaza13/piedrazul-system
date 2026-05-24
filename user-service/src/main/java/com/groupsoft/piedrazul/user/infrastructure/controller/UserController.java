package com.groupsoft.piedrazul.user.infrastructure.controller;

import com.groupsoft.piedrazul.user.application.dto.UserWhatsAppDTO;
import com.groupsoft.piedrazul.user.application.service.UserService;
import com.groupsoft.piedrazul.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/whatsapp-contact")
    public ResponseEntity<Map<String, Object>> registerFromWhatsApp(@RequestBody UserWhatsAppDTO request) {
        User user = service.registerOrGetUserFromWhatsApp(request);
        
        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of(
                        "message", "Paciente procesado exitosamente",
                        "patientId", user.getId()
                )
        );
    }
}