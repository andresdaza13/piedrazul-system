package com.groupsoft.piedrazul.booking.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    // SOFT LINKS (Sin @ManyToOne)
    // Puede ser null si la cita la agendó un agendador por WhatsApp para un paciente no registrado
    @Column(name = "patient_user_id")
    private Long patientUserId; 

    // Referencia al doctor en availability_db
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    // DATOS DE WHATSAPP / PACIENTE INVITADO
    // Obligatorios por requisito para hacer efectiva la cita
    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "full_name")
    private String fullName;

    private String phone;
    
    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate; // Opcional

    private String email; // Opcional
}