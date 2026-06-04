package com.groupsoft.piedrazul.user.infrastructure.config;

import com.groupsoft.piedrazul.user.domain.Repository.UserRepository;
import com.groupsoft.piedrazul.user.domain.model.Role;
import com.groupsoft.piedrazul.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemoUsersInitializer implements ApplicationRunner {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedUser("admin", "Administrador Piedrazul", Role.ADMINISTRATOR, "admin123");
        seedUser("agendador", "Agendador Piedrazul", Role.SCHEDULER, "agendador123");
        seedUser("medico", "Dr. Juan Perez", Role.DOCTOR, "medico123");
        seedUser("paciente", "Maria Lopez", Role.PATIENT, "paciente123");
    }

    private void seedUser(String username, String fullName, Role role, String rawPassword) {
        if (repository.findByUsername(username).isPresent()) {
            return;
        }

        repository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .fullName(fullName)
                .documentNumber("DEMO-" + username.toUpperCase())
                .phone("3000000000")
                .role(role)
                .active(true)
                .build());
    }
}
