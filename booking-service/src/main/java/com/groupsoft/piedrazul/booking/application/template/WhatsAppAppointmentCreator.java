
package com.groupsoft.piedrazul.booking.application.template;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO;
import com.groupsoft.piedrazul.booking.application.service.AppointmentEventPublisher;
import com.groupsoft.piedrazul.booking.domain.event.AppointmentCreatedEvent;
import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.Repository.*;
import org.springframework.stereotype.Component;

@Component
public class WhatsAppAppointmentCreator 
    extends AppointmentCreationTemplate<AppointmentRequestDTO> {

    private final AppointmentEventPublisher eventPublisher;

    public WhatsAppAppointmentCreator(AppointmentRepository repository,
                                       AppointmentEventPublisher eventPublisher) {
        super(repository);
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void validateRequest(AppointmentRequestDTO request) {
        if (request.getWhatsappNumber() == null || request.getWhatsappNumber().isBlank()) {
            throw new IllegalArgumentException("Numero de WhatsApp es obligatorio");
        }
    }

    @Override
    protected Appointment buildAppointment(AppointmentRequestDTO request) {
        return Appointment.builder()
            .patientId(request.getPatientId())
            .doctorId(request.getDoctorId())
            .appointmentDate(request.getAppointmentDate())
            .whatsappNumber(request.getWhatsappNumber())
            .notes(request.getNotes())
            .build();
    }

    @Override
    protected void notifyChannel(Appointment appointment) {
        // Publicar evento a RabbitMQ para enviar confirmacion por WhatsApp
        AppointmentCreatedEvent event = new AppointmentCreatedEvent(
            appointment.getId(),
            appointment.getPatientId(),
            appointment.getDoctorId(),
            appointment.getAppointmentDate(),
            appointment.getWhatsappNumber()
        );
        eventPublisher.publishAppointmentCreatedEvent(event);
    }
}