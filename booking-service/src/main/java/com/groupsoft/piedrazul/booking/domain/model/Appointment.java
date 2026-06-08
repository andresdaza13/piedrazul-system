package com.groupsoft.piedrazul.booking.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// @Entity: indica que esta clase es una entidad JPA que se mapea a una tabla en la base de datos.
// @Table: define el nombre de la tabla ("appointments").
// Lombok: @Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor
// generan automáticamente métodos y constructores, reduciendo código repetitivo.
@Entity
@Table(name = "appointments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    // Identificador único de la cita (Primary Key).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificador del paciente asociado a la cita.
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    // Identificador del médico que atenderá la cita.
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    // Fecha y hora programada de la cita.
    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;

    // Estado actual de la cita (ej. PENDIENTE, CONFIRMADA, CANCELADA, REAGENDADA).
    // Se almacena como texto (STRING) en la base de datos.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    // Número de WhatsApp del paciente, usado para notificaciones.
    @Column(name = "whatsapp_number", length = 20)
    private String whatsappNumber; 
    
    // Notas adicionales sobre la cita (ej. motivo de consulta, observaciones).
    // Se define como tipo TEXT para permitir contenido más largo.
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
