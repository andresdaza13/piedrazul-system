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

    /**
     * PATRON FACTORY METHOD (Creacion): centraliza la construccion de pacientes
     * desde canales externos (WhatsApp / portal web) con reglas de negocio consistentes.
     */
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