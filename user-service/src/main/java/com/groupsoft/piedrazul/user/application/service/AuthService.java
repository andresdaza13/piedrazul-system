package com.groupsoft.piedrazul.user.application.service;

import com.groupsoft.piedrazul.user.application.dto.LoginRequestDTO;
import com.groupsoft.piedrazul.user.application.dto.LoginResponseDTO;
import com.groupsoft.piedrazul.user.domain.Repository.UserRepository;
import com.groupsoft.piedrazul.user.domain.model.Role;
import com.groupsoft.piedrazul.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña incorrectos"));

        if (!user.isActive()) {
            throw new IllegalArgumentException("Usuario inactivo. Contacte al administrador.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Usuario o contraseña incorrectos");
        }

        return LoginResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .role(user.getRole())
                .homeRoute(resolveHomeRoute(user.getRole()))
                .build();
    }

    private String resolveHomeRoute(Role role) {
        return switch (role) {
            case ADMINISTRATOR -> "/admin";
            case PATIENT -> "/paciente";
            case DOCTOR, SCHEDULER -> "/agendador";
        };
    }
}
