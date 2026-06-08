package com.groupsoft.piedrazul.user.domain.model;
// Paquete del dominio User, contiene la entidad principal User

import jakarta.persistence.*; 
// Anotaciones de JPA para mapear la entidad a la base de datos

import lombok.*; 
// Anotaciones de Lombok para generar automáticamente getters, setters, constructores y builder

import java.time.LocalDate; 
// Clase estándar de Java para manejar fechas

import java.util.UUID; 
// Clase estándar de Java para generar identificadores únicos (contraseñas UUID)

/**
 * Entidad User que representa a los usuarios del sistema.
 * Se mapea a la tabla "app_users" en la base de datos.
 */
@Entity
@Table(name = "app_users")
@Getter 
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    // Identificador único autogenerado en la BD
    private Long id;

    @Column(unique = true, nullable = false) 
    // Username único y obligatorio
    private String username;

    @Column(nullable = false) 
    // Contraseña obligatoria
    private String password;

    private String fullName; 
    // Nombre completo del usuario
    
    @Column(unique = true) 
    // Número de documento único
    private String documentNumber;
    
    private String phone; 
    // Teléfono del usuario

    @Enumerated(EnumType.STRING) 
    // Rol del usuario (PATIENT, DOCTOR, etc.)
    private Role role;

    private boolean active; 
    // Estado de actividad del usuario

    @Column(name = "doctor_id") 
    // Relación opcional con un doctor
    private Long doctorId;

    // --- NUEVOS CAMPOS REQUISITO 2 ---
    @Enumerated(EnumType.STRING) 
    // Género del usuario
    private Gender gender;

    private LocalDate birthDate; 
    // Fecha de nacimiento (opcional)
    
    @Column(unique = true) 
    // Correo electrónico único (opcional)
    private String email;

    /**
     * Patrón de fábrica para creación de pacientes desde WhatsApp.
     * Este método está diseñado para crear una instancia de User específicamente para pacientes que se registran a través de WhatsApp.
     * En el contexto de la aplicación, es común que los pacientes no tengan una cuenta tradicional con credenciales web,
     * sino que se comuniquen y registren a través de WhatsApp.
     * 
     * El método toma los datos básicos obtenidos de WhatsApp (documento, nombre, teléfono, género, fecha de nacimiento y correo)
     * y los mapea a un objeto User. Además:
     * - Asigna automáticamente el rol de PATIENT.
     * - Genera un username temporal basado en el número de documento.
     * - Genera una contraseña UUID única para asegurar integridad en la BD.
     * - Marca al usuario como activo por defecto.
     */
    public static User createPatientFromWhatsApp(String documentNumber, String firstName, 
                                                 String lastName, String phone, 
                                                 Gender gender, LocalDate birthDate, String email) {
        return User.builder()
                // Concatenación de nombre y apellido con trim para evitar espacios extra
                .fullName(firstName.trim() + " " + lastName.trim())
                .documentNumber(documentNumber)
                .phone(phone)
                .gender(gender)
                .birthDate(birthDate)
                .email(email)
                // Configuración automática de rol y estado
                .role(Role.PATIENT)
                .active(true)
                // Username temporal = número de documento
                .username(documentNumber) 
                // Contraseña UUID generada automáticamente
                .password(UUID.randomUUID().toString()) 
                .build();
    }
}
