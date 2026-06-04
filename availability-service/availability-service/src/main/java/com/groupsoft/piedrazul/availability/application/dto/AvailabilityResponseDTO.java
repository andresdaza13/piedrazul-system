package com.groupsoft.piedrazul.availability.application.dto;

import lombok.Builder;
import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
public class AvailabilityResponseDTO {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer intervalMinutes;
    private boolean active;
}
