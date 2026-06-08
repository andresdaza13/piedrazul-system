package com.groupsoft.piedrazul.availability.application.dto;

import lombok.Data;

// @Data (Lombok): genera automáticamente getters, setters, equals, hashCode y toString.
// Esto simplifica el código y evita escribir métodos repetitivos.
@Data
public class DoctorRequestDTO {

    // Nombre completo del médico.
    // Se utiliza para identificarlo en la plataforma y mostrarlo en las respuestas.
    private String fullName;

    // Especialidad del médico (ej. pediatría, cardiología, fisioterapia).
    // Permite clasificar y filtrar médicos según su área de atención.
    private String specialty;
}
