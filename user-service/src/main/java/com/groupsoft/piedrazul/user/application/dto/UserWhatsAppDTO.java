package com.groupsoft.piedrazul.user.application.dto;

import com.groupsoft.piedrazul.user.domain.model.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserWhatsAppDTO {
    @NotBlank(message = "El documento es obligatorio")
    private String documentNumber;

    @NotBlank(message = "Los nombres son obligatorios")
    private String firstName;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String lastName;

    @NotBlank(message = "El celular es obligatorio")
    private String phone;

    @NotNull(message = "El genero es obligatorio")
    private Gender gender;

    private LocalDate birthDate;
    private String email;
}
