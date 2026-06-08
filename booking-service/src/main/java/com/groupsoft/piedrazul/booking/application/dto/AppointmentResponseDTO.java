package com.groupsoft.piedrazul.booking.application.dto;

import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

// @Data (Lombok): genera automáticamente getters, setters, equals, hashCode y toString.
// Esto reduce código repetitivo y mejora la mantenibilidad.
// @Builder (Lombok): habilita el patrón Builder, permitiendo construir objetos
// de manera más legible y flexible, útil en respuestas REST.
@Data
@Builder
public class AppointmentResponseDTO {

    // Identificador único de la cita en la base de datos.
    private Long id;

    // Identificador del paciente asociado a la cita.
    private Long patientId;

    // Identificador del médico/terapista que atenderá la cita.
    private Long doctorId;

    // Fecha y hora programada de la cita.
    // Se usa LocalDateTime para manejar fecha y hora en un solo campo.
    private LocalDateTime appointmentDate;

    // Estado actual de la cita (ej. PENDIENTE, CONFIRMADA, CANCELADA, REAGENDADA).
    // Este campo refleja el uso del patrón State en el dominio.
    private AppointmentStatus status;

    // Número de WhatsApp del paciente, usado para notificaciones.
    private String whatsappNumber;

    // Notas adicionales registradas en la cita (ej. motivo de consulta, observaciones).
    private String notes;
}
