package com.groupsoft.piedrazul.booking.infrastructure.event;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO;
import com.groupsoft.piedrazul.booking.application.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * PATRÓN OBSERVER (Comportamiento):
 * Este componente actúa como un Observador concreto.
 * Se suscribe a la cola de RabbitMQ y reacciona automáticamente
 * cuando se recibe un evento de creación de cita desde WhatsApp.
 */
@Component
@RequiredArgsConstructor
public class WhatsAppAppointmentListener {

    // Servicio de citas: contiene la lógica de negocio para crear citas.
    private final AppointmentService appointmentService;

    /**
     * Método que escucha la cola "whatsapp.appointment.queue".
     * Cada vez que llega un mensaje (AppointmentRequestDTO), este método se ejecuta.
     *
     * @param eventPayload Datos de la cita enviados desde el user-service vía RabbitMQ.
     */
    @RabbitListener(queues = "whatsapp.appointment.queue")
    public void handleWhatsAppAppointmentRequest(AppointmentRequestDTO eventPayload) {
        try {
            // Se delega la creación de la cita al AppointmentService.
            // Aquí se validan intervalos y reglas de negocio.
            appointmentService.createAppointment(eventPayload);

            // Log informativo: confirma que la cita fue creada de manera asíncrona.
            System.out.println("Cita creada asíncronamente desde evento de WhatsApp para paciente ID: " + eventPayload.getPatientId());
        } catch (Exception e) {
            // Manejo de errores: si ocurre un problema, se imprime en consola.
            // En un entorno real, se usaría una Dead Letter Queue para almacenar mensajes fallidos.
            System.err.println("Error procesando la cita de WhatsApp: " + e.getMessage());
        }
    }
}
