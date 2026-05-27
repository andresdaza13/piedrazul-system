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
        
        // Estrategia Find-or-Create: Buscamos al paciente por documento o celular
        User user = repository.findByDocumentNumber(request.getDocumentNumber())
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

        // PATRÓN OBSERVER: Emitimos el evento asíncrono
        // Enviamos el request original a la cola para que el booking-service lo capture y cree la cita
        rabbitTemplate.convertAndSend(RabbitMQConfig.WHATSAPP_QUEUE, request);

        return user;
    }
}