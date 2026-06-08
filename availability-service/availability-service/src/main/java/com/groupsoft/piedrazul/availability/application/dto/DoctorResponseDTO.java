package com.groupsoft.piedrazul.availability.application.dto;

import lombok.Builder;
import lombok.Data;

// @Data (Lombok): genera automáticamente getters, setters, equals, hashCode y toString.
// @Builder (Lombok): habilita el patrón Builder, permitiendo construir objetos
// de manera flexible y legible, ideal para respuestas REST.
@Data
@Builder
public class DoctorResponseDTO {

    // Identificador único del médico en la base de datos.
    private Long id;

    // Nombre completo del médico.
    private String fullName;

    // Especialidad del médico (ej. pediatría, cardiología, fisioterapia).
    private String specialty;

    // Estado del médico en el sistema (true = activo, false = inactivo).
    // Permite habilitar o deshabilitar médicos sin eliminarlos.
    private boolean active;
}
