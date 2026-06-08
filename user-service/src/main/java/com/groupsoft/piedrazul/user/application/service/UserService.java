package com.groupsoft.piedrazul.user.application.service;
// Paquete de servicios de aplicación del bounded context User

import com.groupsoft.piedrazul.user.application.dto.UserWhatsAppDTO; 
// DTO que representa los datos de usuario recibidos desde WhatsApp

import com.groupsoft.piedrazul.user.domain.model.User; 
// Entidad del dominio que representa un usuario

import com.groupsoft.piedrazul.user.domain.Repository.UserRepository; 
// Repositorio JPA para acceder y persistir usuarios en la base de datos

import com.groupsoft.piedrazul.user.infrastructure.config.RabbitMQConfig; 
// Configuración de RabbitMQ, contiene nombres de colas

import lombok.RequiredArgsConstructor; 
// Anotación de Lombok que genera constructor con parámetros obligatorios (final)

import org.springframework.amqp.rabbit.core.RabbitTemplate; 
// Componente de Spring AMQP para enviar mensajes a RabbitMQ

import org.springframework.stereotype.Service; 
// Anotación que marca la clase como un servicio de Spring

import org.springframework.transaction.annotation.Transactional; 
// Anotación para manejar transacciones en métodos de servicio

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository; 
    // Repositorio para operaciones de persistencia de usuarios
    
    // Inyectamos el template de RabbitMQ para emitir mensajes (Patrón Observer)
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public User registerOrGetUserFromWhatsApp(UserWhatsAppDTO request) {
        
        // Estrategia Find-or-Create: Buscamos al paciente por documento o celular
        User user = repository.findByDocumentNumber(request.getDocumentNumber())
                .orElseGet(() -> repository.findByPhone(request.getPhone())
                .orElseGet(() -> {
                    
                    // Invocamos el patrón Factory Method para crear un nuevo usuario paciente
                    User newUser = User.createPatientFromWhatsApp(
                            request.getDocumentNumber(),
                            request.getFirstName(),
                            request.getLastName(),
                            request.getPhone(),
                            request.getGender(),
                            request.getBirthDate(),
                            request.getEmail()
                    );
                    
                    // Guardamos el nuevo usuario en la base de datos
                    return repository.save(newUser);
                }));

        // PATRÓN OBSERVER: Emitimos el evento asíncrono
        // Enviamos el request original a la cola para que el booking-service lo capture y cree la cita
        rabbitTemplate.convertAndSend(RabbitMQConfig.WHATSAPP_QUEUE, request);

        return user; // Retornamos el usuario encontrado o creado
    }
}
