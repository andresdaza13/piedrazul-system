package com.groupsoft.piedrazul.booking.infrastructure.event;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO;
import com.groupsoft.piedrazul.booking.application.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * PATRÓN OBSERVER (Comportamiento):
 * Actúa como el Observador concreto que reacciona a los eventos emitidos en la cola de RabbitMQ.
 */
@Component
@RequiredArgsConstructor
public class WhatsAppAppointmentListener {

    private final AppointmentService appointmentService;

    // Escucha la misma cola que definimos en el user-service
    @RabbitListener(queues = "whatsapp.appointment.queue")
    public void handleWhatsAppAppointmentRequest(AppointmentRequestDTO eventPayload) {
        try {
            // El booking-service recibe el evento y crea la cita con la validación de intervalos
            appointmentService.createAppointment(eventPayload);
            System.out.println("Cita creada asíncronamente desde evento de WhatsApp para paciente ID: " + eventPayload.getPatientId());
        } catch (Exception e) {
            System.err.println("Error procesando la cita de WhatsApp: " + e.getMessage());
            // Aquí se implementaría una Dead Letter Queue en un entorno productivo real
        }
    }
}