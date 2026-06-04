package com.groupsoft.piedrazul.booking.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RescheduleRequestDTO {
    @NotNull(message = "La nueva fecha es obligatoria")
    private LocalDateTime newAppointmentDate;

    @NotNull(message = "El responsable es obligatorio")
    private Long responsibleUserId;

    @NotBlank(message = "El nombre del responsable es obligatorio")
    private String responsibleName;
}
