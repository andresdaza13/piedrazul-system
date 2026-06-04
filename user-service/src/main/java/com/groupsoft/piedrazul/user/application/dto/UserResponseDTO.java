package com.groupsoft.piedrazul.user.application.dto;

import com.groupsoft.piedrazul.user.domain.model.Gender;
import com.groupsoft.piedrazul.user.domain.model.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String documentNumber;
    private String phone;
    private Gender gender;
    private LocalDate birthDate;
    private String email;
    private Role role;
}
