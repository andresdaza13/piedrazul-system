package com.groupsoft.piedrazul.user.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

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
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String fullName;
    
    @Column(unique = true)
    private String documentNumber;
    
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean active;

    @Column(name = "doctor_id")
    private Long doctorId;

    // --- NUEVOS CAMPOS REQUISITO 2 ---
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDate; // Opcional según requerimiento
    
    @Column(unique = true)
    private String email; // Opcional según requerimiento

   // patron de fábrica para creación de pacientes desde WhatsApp
   //explicame como funciona este método y por qué es útil en el contexto de tu aplicación
   // Este método está diseñado para crear una instancia de User específicamente para pacientes que se registran a través de WhatsApp.
   // En el contexto de tu aplicación, es común que los pacientes no tengan una cuenta tradicional con credenciales web, sino que se comuniquen y registren a través de WhatsApp.
   // Este método toma los datos básicos que se pueden obtener de WhatsApp (como el número de documento, nombre, teléfono, género, fecha de nacimiento y correo electrónico) y los mapea a un objeto User.
   // Además, asigna automáticamente el rol de PATIENT y genera un username y password únicos para asegurar la integridad de la base de datos, aunque estos pacientes no usarán credenciales web inicialmente.  
    public static User createPatientFromWhatsApp(String documentNumber, String firstName, 
                                                 String lastName, String phone, 
                                                 Gender gender, LocalDate birthDate, String email) {
        return User.builder()
                // Mapeo inteligente: Concatenamos para respetar tu campo fullName existente
                .fullName(firstName.trim() + " " + lastName.trim())
                .documentNumber(documentNumber)
                .phone(phone)
                .gender(gender)
                .birthDate(birthDate)
                .email(email)
                // Autoconfiguración de seguridad y roles
                .role(Role.PATIENT)
                .active(true)
                // Al no tener credenciales web aún, aseguramos la integridad de la BD
                .username(documentNumber) 
                .password(UUID.randomUUID().toString()) 
                .build();
    }
}