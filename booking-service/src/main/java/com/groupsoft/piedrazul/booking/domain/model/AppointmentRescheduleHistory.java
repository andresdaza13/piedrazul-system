package com.groupsoft.piedrazul.booking.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_reschedule_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRescheduleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;

    @Column(name = "previous_date", nullable = false)
    private LocalDateTime previousDate;

    @Column(name = "new_date", nullable = false)
    private LocalDateTime newDate;

    @Column(name = "responsible_user_id", nullable = false)
    private Long responsibleUserId;

    @Column(name = "responsible_name", nullable = false)
    private String responsibleName;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
}
