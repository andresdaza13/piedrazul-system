package com.groupsoft.piedrazul.availability.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialty;
    
    // Aquí a futuro implementaremos la configuración del administrador:
    // intervalMinutes, attentionDays, etc.
}