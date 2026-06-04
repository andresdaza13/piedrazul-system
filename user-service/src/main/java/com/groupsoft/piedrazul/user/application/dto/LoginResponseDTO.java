package com.groupsoft.piedrazul.user.application.dto;

import com.groupsoft.piedrazul.user.domain.model.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDTO {

    private Long id;
    private String fullName;
    private String username;
    private Role role;
    private String homeRoute;
}
