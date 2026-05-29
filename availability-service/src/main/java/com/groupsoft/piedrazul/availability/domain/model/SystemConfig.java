package com.groupsoft.piedrazul.availability.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_config")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig {

    @Id
    private Long id;

    @Column(name = "booking_window_weeks", nullable = false)
    private int bookingWindowWeeks;
}
