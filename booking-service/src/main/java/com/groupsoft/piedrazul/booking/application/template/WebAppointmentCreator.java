package com.groupsoft.piedrazul.booking.application.template;

import com.groupsoft.piedrazul.booking.application.dto.WebBookingRequestDTO;
import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.infrastructure.adapter.AvailabilityClientAdapter;
import com.groupsoft.piedrazul.booking.domain.Repository.*;
import org.springframework.stereotype.Component;
import java.time.LocalTime;
import java.util.List;

@Component
public class WebAppointmentCreator 
    extends AppointmentCreationTemplate<WebBookingRequestDTO> {

    private final AvailabilityClientAdapter availabilityAdapter;

    public WebAppointmentCreator(AppointmentRepository repository,
                                  AvailabilityClientAdapter availabilityAdapter) {
        super(repository);
        this.availabilityAdapter = availabilityAdapter;
    }

    @Override
    protected void validateRequest(WebBookingRequestDTO request) {
        // En web SI validamos que la franja este en la lista disponible
        List<LocalTime> slots = availabilityAdapter.getAvailableSlots(
            request.getDoctorId(), request.getAppointmentDate()
        );
        if (!slots.contains(request.getAppointmentTime())) {
            throw new IllegalArgumentException(
                "La franja seleccionada no esta disponible"
            );
        }
    }

    @Override
    protected Appointment buildAppointment(WebBookingRequestDTO request) {
        return Appointment.builder()
            .patientId(request.getPatientId())
            .doctorId(request.getDoctorId())
            .appointmentDate(
                request.getAppointmentDate().atTime(request.getAppointmentTime())
            )
            .whatsappNumber("WEB-BOOKING")
            .notes(request.getNotes())
            .build();
    }

    @Override
    protected void notifyChannel(Appointment appointment) {
        // En web no enviamos WhatsApp, podriamos enviar email
        System.out.println("Cita web creada — enviar email al paciente: " 
            + appointment.getPatientId());
    }
}