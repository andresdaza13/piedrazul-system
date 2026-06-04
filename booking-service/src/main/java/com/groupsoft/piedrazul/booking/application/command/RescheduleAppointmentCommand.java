package com.groupsoft.piedrazul.booking.application.command;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class RescheduleAppointmentCommand implements Command<AppointmentResponseDTO> {
    Long appointmentId;
    LocalDateTime newAppointmentDate;
    Long responsibleUserId;
    String responsibleName;
}
