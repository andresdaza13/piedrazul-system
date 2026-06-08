package com.groupsoft.piedrazul.availability.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

// @Entity: indica que esta clase es una entidad JPA.
// @Table: define el nombre de la tabla en la base de datos ("doctor_availability").
// Lombok: @Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor
// generan automáticamente métodos y constructores, reduciendo código repetitivo.
@Entity
@Table(name = "doctor_availability") // Nombre más descriptivo en BD
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Availability {

    // Identificador único de la disponibilidad (Primary Key).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la entidad Doctor (ManyToOne).
    // Cada disponibilidad pertenece a un médico específico.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Día de la semana en que aplica la disponibilidad (ej. MONDAY, TUESDAY).
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    // Hora de inicio de la disponibilidad.
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // Hora de fin de la disponibilidad.
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Intervalo en minutos entre cada cita disponible (ej. 30 minutos).
    @Column(name = "interval_minutes", nullable = false)
    private Integer intervalMinutes;

    // Estado de la disponibilidad (true = activa, false = inactiva).
    @Column(name = "active", nullable = false)
    private boolean active;
}
