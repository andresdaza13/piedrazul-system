package com.groupsoft.piedrazul.availability.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemConfigDTO {
    private int bookingWindowWeeks;
}
