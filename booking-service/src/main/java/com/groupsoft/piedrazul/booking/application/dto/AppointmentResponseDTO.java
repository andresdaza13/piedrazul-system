package com.groupsoft.piedrazul.booking.application.dto;

import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

// @Data (Lombok): genera automáticamente getters, setters, equals, hashCode y toString.
// @Builder (Lombok): permite construir objetos de manera flexible usando el patrón Builder.
// Esto es útil cuando se quiere crear instancias con solo algunos campos o con mayor legibilidad.
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
    private LocalDateTime appointmentDate;

    // Estado actual de la cita (ej. PENDIENTE, CONFIRMADA, CANCELADA, REAGENDADA).
    // Se define en el dominio como un enum AppointmentStatus.
    private AppointmentStatus status;

    // Número de WhatsApp del paciente, usado para notificaciones.
    private String whatsappNumber;

    // Notas adicionales registradas en la cita (ej. motivo de consulta, observaciones).
    private String notes;
}
