package com.groupsoft.piedrazul.booking.infrastructure.event;

import com.groupsoft.piedrazul.booking.domain.event.AppointmentCreatedEvent;
import com.groupsoft.piedrazul.booking.infrastructure.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * PATRON OBSERVER (Comportamiento): observador de notificaciones cuando se crea una cita.
 * Complementa el flujo asincrono de WhatsApp con auditoria de citas confirmadas.
 */
@Slf4j
@Component
public class AppointmentCreatedNotificationListener {

    @RabbitListener(queues = RabbitMQConfig.WHATSAPP_QUEUE)
    public void onAppointmentCreated(AppointmentCreatedEvent event) {
        log.info(
                "Observer: cita {} registrada para paciente {} con medico {} en {}",
                event.appointmentId(),
                event.patientId(),
                event.doctorId(),
                event.appointmentDate()
        );
    }
}
