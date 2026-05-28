package com.groupsoft.piedrazul.availability.application.dto;

import lombok.Builder;

@Data
@Builder
public class DoctorResponseDTO {
    private Long id;
    private String fullName;
    private String specialty;
    private boolean active;
}
