package com.groupsoft.piedrazul.booking.application.service;

import com.groupsoft.piedrazul.booking.domain.event.AppointmentCreatedEvent;
import com.groupsoft.piedrazul.booking.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

// @Component: indica que esta clase es un bean administrado por Spring.
// @RequiredArgsConstructor: genera automáticamente el constructor con los atributos finales.
@Component
@RequiredArgsConstructor
public class AppointmentEventPublisher {

    // RabbitTemplate: cliente de Spring AMQP para enviar mensajes a RabbitMQ.
    private final RabbitTemplate rabbitTemplate;

    /**
     * Publica un evento de creación de cita en RabbitMQ.
     * Este evento será consumido por otros microservicios (ej. notificaciones por WhatsApp).
     *
     * @param event Evento que contiene los datos de la cita creada.
     */
    public void publishAppointmentCreatedEvent(AppointmentCreatedEvent event) {
        // Envía el evento al exchange definido en RabbitMQConfig,
        // usando la routing key "appointment.created".
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME, 
                RabbitMQConfig.ROUTING_KEY, 
                event
        );

        // Log informativo: confirma que el evento fue emitido correctamente.
        System.out.println("Evento emitido a RabbitMQ: " + event.appointmentId());
    }
}
