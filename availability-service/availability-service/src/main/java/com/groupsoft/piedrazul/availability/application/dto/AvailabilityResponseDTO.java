package com.groupsoft.piedrazul.availability.application.dto;

import lombok.Builder;
import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalTime;

// @Data (Lombok): genera automáticamente getters, setters, equals, hashCode y toString.
// @Builder (Lombok): habilita el patrón Builder, permitiendo construir objetos
// de manera flexible y legible, ideal para respuestas REST.
@Data
@Builder
public class AvailabilityResponseDTO {

    // Identificador único del registro de disponibilidad en la base de datos.
    private Long id;

    // Identificador del médico asociado a esta disponibilidad.
    private Long doctorId;

    // Nombre del médico (campo adicional para mostrar información más clara al cliente).
    private String doctorName;

    // Día de la semana en que aplica la disponibilidad (ej. MONDAY, TUESDAY).
    private DayOfWeek dayOfWeek;

    // Hora de inicio de la disponibilidad.
    private LocalTime startTime;

    // Hora de fin de la disponibilidad.
    private LocalTime endTime;

    // Intervalo en minutos entre cada cita disponible (ej. 30 minutos).
    private Integer intervalMinutes;

    // Estado de la disponibilidad (true = activa, false = inactiva).
    private boolean active;
}
