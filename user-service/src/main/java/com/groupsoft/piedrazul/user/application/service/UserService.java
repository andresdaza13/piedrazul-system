package com.groupsoft.piedrazul.user.application.service;

import com.groupsoft.piedrazul.user.application.dto.UserWhatsAppDTO;
import com.groupsoft.piedrazul.user.domain.model.User;
import com.groupsoft.piedrazul.user.domain.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    @Transactional
    public User registerOrGetUserFromWhatsApp(UserWhatsAppDTO request) {
        
        // Estrategia Find-or-Create: Buscamos al paciente por documento o celular
        return repository.findByDocumentNumber(request.getDocumentNumber())
                .orElseGet(() -> repository.findByPhone(request.getPhone())
                .orElseGet(() -> {
                    
                    // Invocamos el patrón Factory Method
                    User newUser = User.createPatientFromWhatsApp(
                            request.getDocumentNumber(),
                            request.getFirstName(),
                            request.getLastName(),
                            request.getPhone(),
                            request.getGender(),
                            request.getBirthDate(),
                            request.getEmail()
                    );
                    
                    return repository.save(newUser);
                }));
    }
}