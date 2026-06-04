package com.groupsoft.piedrazul.availability.infrastructure.controller;

import com.groupsoft.piedrazul.availability.application.dto.SystemConfigDTO;
import com.groupsoft.piedrazul.availability.application.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/system-config")
@RequiredArgsConstructor
@Tag(name = "System Config", description = "Parametros globales del agendamiento")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping
    @Operation(summary = "Obtener configuracion del sistema",
               description = "Requisito 4: ventana de agendamiento en semanas")
    public ResponseEntity<SystemConfigDTO> getConfig() {
        return ResponseEntity.ok(systemConfigService.getConfig());
    }

    @PutMapping("/booking-window")
    @Operation(summary = "Configurar ventana de agendamiento",
               description = "Requisito 4: semanas hacia adelante en las que se habilitan citas")
    public ResponseEntity<SystemConfigDTO> updateBookingWindow(
            @RequestBody Map<String, Integer> body) {
        Integer weeks = body.get("bookingWindowWeeks");
        if (weeks == null) {
            throw new IllegalArgumentException("bookingWindowWeeks es obligatorio");
        }
        return ResponseEntity.ok(systemConfigService.updateBookingWindowWeeks(weeks));
    }
}
