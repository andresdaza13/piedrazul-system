package com.groupsoft.piedrazul.booking.application.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

// @Data (Lombok): genera automáticamente getters, setters, equals, hashCode y toString.
// Esto simplifica el código y evita escribir métodos repetitivos.
@Data
public class WebBookingRequestDTO {

    // Identificador único del paciente que solicita la cita.
    private Long patientId;

    // Identificador único del médico/terapista con quien se agenda la cita.
    private Long doctorId;

    // Fecha seleccionada para la cita.
    // Se usa LocalDate porque solo se requiere la parte de fecha (sin hora).
    private LocalDate appointmentDate;

    // Hora seleccionada para la cita.
    // Se usa LocalTime porque solo se requiere la parte de hora (sin fecha).
    private LocalTime appointmentTime;

    // Notas adicionales que el paciente puede incluir (ej. motivo de consulta).
    private String notes;
}
