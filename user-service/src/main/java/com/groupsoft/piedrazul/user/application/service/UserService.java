package com.groupsoft.piedrazul.user.application.service;

import com.groupsoft.piedrazul.user.application.dto.UserWhatsAppDTO;
import com.groupsoft.piedrazul.user.domain.model.User;
import com.groupsoft.piedrazul.user.domain.Repository.UserRepository;
import com.groupsoft.piedrazul.user.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    
    // Inyectamos el template de RabbitMQ para emitir mensajes (Patrón Observer)
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public User registerOrGetUserFromWhatsApp(UserWhatsAppDTO request) {
        User user = findOrCreatePatient(request);
        rabbitTemplate.convertAndSend(RabbitMQConfig.WHATSAPP_QUEUE, request);
        return user;
    }

    @Transactional
    public User registerWebPatient(UserWhatsAppDTO request) {
        return findOrCreatePatient(request);
    }

    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private User findOrCreatePatient(UserWhatsAppDTO request) {
        return repository.findByDocumentNumber(request.getDocumentNumber())
                .orElseGet(() -> repository.findByPhone(request.getPhone())
                .orElseGet(() -> repository.save(User.createPatientFromWhatsApp(
                        request.getDocumentNumber(),
                        request.getFirstName(),
                        request.getLastName(),
                        request.getPhone(),
                        request.getGender(),
                        request.getBirthDate(),
                        request.getEmail()
                ))));
    }
}