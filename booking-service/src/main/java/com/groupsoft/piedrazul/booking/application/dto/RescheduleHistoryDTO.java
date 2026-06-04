package com.groupsoft.piedrazul.booking.application.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class RescheduleHistoryDTO {
    private Long id;
    private Long appointmentId;
    private LocalDateTime previousDate;
    private LocalDateTime newDate;
    private Long responsibleUserId;
    private String responsibleName;
    private LocalDateTime changedAt;
}
