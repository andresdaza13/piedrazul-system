package com.groupsoft.piedrazul.booking.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

// @Data (Lombok): genera automáticamente getters, setters, equals, hashCode y toString.
// Esto simplifica el código y evita escribir métodos repetitivos.
@Data
public class AppointmentRequestDTO {

    // Identificador único del paciente que solicita la cita.
    private Long patientId;

    // Identificador único del médico/terapista con quien se agenda la cita.
    private Long doctorId;

    // Fecha y hora exacta de la cita.
    // Se usa LocalDateTime para manejar tanto la fecha como la hora en un solo campo.
    private LocalDateTime appointmentDate;

    // Número de WhatsApp del paciente.
    // Permite enviar notificaciones y confirmaciones de manera asíncrona.
    private String whatsappNumber;

    // Notas adicionales sobre la cita (ej. motivo de consulta, observaciones).
    private String notes;
}
