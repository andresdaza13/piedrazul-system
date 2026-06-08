package com.groupsoft.piedrazul.availability.application.dto;

import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalTime;

// @Data (Lombok): genera automáticamente getters, setters, equals, hashCode y toString.
// Esto simplifica el código y evita escribir métodos repetitivos.
@Data
public class AvailabilityRequestDTO {

    // Identificador único del médico cuya disponibilidad se está configurando.
    private Long doctorId;

    // Día de la semana en que aplica la disponibilidad (ej. MONDAY, TUESDAY).
    private DayOfWeek dayOfWeek;

    // Hora de inicio de la disponibilidad (ej. 08:00 AM).
    private LocalTime startTime;

    // Hora de fin de la disponibilidad (ej. 05:00 PM).
    private LocalTime endTime;

    // Intervalo en minutos entre cada cita disponible (ej. 30 minutos).
    private Integer intervalMinutes;
}
