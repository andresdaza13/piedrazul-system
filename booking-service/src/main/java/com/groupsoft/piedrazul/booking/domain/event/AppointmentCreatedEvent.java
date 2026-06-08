package com.groupsoft.piedrazul.booking.domain.event;

import java.time.LocalDateTime;

// Usamos Record de Java 21 para crear un DTO inmutable de forma ultra limpia.
// Los records son ideales para representar datos inmutables y concisos,
// ya que generan automáticamente equals, hashCode y toString.
public record AppointmentCreatedEvent(
        Long appointmentId,     // Identificador único de la cita creada.
        Long patientId,         // Identificador del paciente asociado.
        Long doctorId,          // Identificador del médico que atenderá la cita.
        LocalDateTime appointmentDate, // Fecha y hora de la cita.
        String whatsappNumber   // Número de WhatsApp para notificaciones.
) {}
