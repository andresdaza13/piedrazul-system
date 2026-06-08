package com.groupsoft.piedrazul.user.application.dto;
// Paquete de DTOs de la capa de aplicación del bounded context User

import com.groupsoft.piedrazul.user.domain.model.Gender; 
// Enum del dominio que define el género del usuario

import lombok.Data; 
// Anotación de Lombok que genera automáticamente getters, setters, equals, hashCode y toString

import java.time.LocalDate; 
// Clase estándar de Java para manejar fechas (sin hora)

/**
 * DTO que representa los datos de un usuario recibidos desde WhatsApp.
 * Se utiliza para transportar información entre capas sin lógica adicional.
 */
@Data
public class UserWhatsAppDTO {
    private String documentNumber; // Número de documento del usuario
    private String firstName;      // Nombre del usuario
    private String lastName;       // Apellido del usuario
    private String phone;          // Teléfono del usuario (ej. WhatsApp)
    private Gender gender;         // Género del usuario (HOMBRE, MUJER, OTRO)
    private LocalDate birthDate;   // Fecha de nacimiento del usuario
    private String email;          // Correo electrónico del usuario
}
