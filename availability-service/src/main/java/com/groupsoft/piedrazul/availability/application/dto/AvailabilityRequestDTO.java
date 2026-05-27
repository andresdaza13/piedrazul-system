package com.groupsoft.piedrazul.availability.application.dto;

import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class AvailabilityRequestDTO {
    private Long doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer intervalMinutes;
}