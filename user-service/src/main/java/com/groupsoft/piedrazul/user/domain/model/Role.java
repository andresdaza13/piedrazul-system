package com.groupsoft.piedrazul.user.domain.model;
// Paquete del dominio User, contiene modelos y enums relacionados con usuarios

/**
 * Enum que representa los roles posibles de un usuario dentro del sistema.
 * Se utiliza en la entidad User para definir permisos y responsabilidades.
 */
public enum Role {
    SCHEDULER,      // Usuario encargado de agendar citas
    PATIENT,        // Usuario paciente que solicita y recibe atención
    DOCTOR,         // Usuario médico o terapeuta que atiende pacientes
    ADMINISTRATOR   // Usuario administrador con privilegios de gestión
}
