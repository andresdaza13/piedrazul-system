package com.groupsoft.piedrazul.availability.domain.model;

import jakarta.persistence.*;
import lombok.*;

// @Entity: indica que esta clase es una entidad JPA.
// @Table: define el nombre de la tabla en la base de datos ("doctors").
// Lombok: @Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor
// generan automáticamente métodos y constructores, reduciendo código repetitivo.
@Entity
@Table(name = "doctors")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    // Identificador único del médico (Primary Key).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre completo del médico.
    private String fullName;

    // Especialidad del médico (ej. pediatría, cardiología, fisioterapia).
    private String specialty;

    // Estado del médico en el sistema (true = activo, false = inactivo).
    // Permite habilitar o deshabilitar médicos sin eliminarlos.
    private boolean active;
}
