package com.groupsoft.piedrazul.booking.application.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class WebBookingRequestDTO {
    private Long patientId;
    private Long doctorId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String notes;
}