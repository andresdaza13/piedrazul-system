package com.groupsoft.piedrazul.booking.application.dto;

import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentResponseDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private String patientDocument;
    private Long doctorId;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private String whatsappNumber;
    private String notes;
}