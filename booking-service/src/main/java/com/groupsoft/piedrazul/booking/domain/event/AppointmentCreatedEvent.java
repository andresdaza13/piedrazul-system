package com.groupsoft.piedrazul.booking.domain.event;

import java.time.LocalDateTime;

// Usamos Record de Java 21 para crear un DTO inmutable de forma ultra limpia
public record AppointmentCreatedEvent(
        Long appointmentId,
        Long patientId,
        Long doctorId,
        LocalDateTime appointmentDate,
        String whatsappNumber
) {}