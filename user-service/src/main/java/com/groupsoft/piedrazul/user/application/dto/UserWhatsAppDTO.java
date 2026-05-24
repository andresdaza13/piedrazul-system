package com.groupsoft.piedrazul.user.application.dto;

import com.groupsoft.piedrazul.user.domain.model.Gender;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserWhatsAppDTO {
    private String documentNumber;
    private String firstName;
    private String lastName;
    private String phone;
    private Gender gender;
    private LocalDate birthDate;
    private String email;
}