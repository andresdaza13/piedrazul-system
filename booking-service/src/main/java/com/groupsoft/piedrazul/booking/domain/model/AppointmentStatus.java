package com.groupsoft.piedrazul.booking.domain.model;

// Enum que define los posibles estados de una cita médica.
// Se usa en la entidad Appointment y en el DTO de respuesta.
// Representa el ciclo de vida de una cita en el sistema Piedrazul.
public enum AppointmentStatus {
    PENDING,   // La cita fue creada pero aún no confirmada.
    CONFIRMED, // La cita fue confirmada por el paciente o el médico.
    CANCELLED, // La cita fue cancelada (por paciente o médico).
    COMPLETED  // La cita ya se realizó y fue marcada como completada.
}
