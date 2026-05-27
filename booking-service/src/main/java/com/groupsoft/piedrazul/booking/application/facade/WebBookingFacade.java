package com.groupsoft.piedrazul.booking.application.facade;

import com.groupsoft.piedrazul.booking.application.dto.WebBookingRequestDTO;
import com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO;
import com.groupsoft.piedrazul.booking.application.service.AppointmentService;
import com.groupsoft.piedrazul.booking.infrastructure.adapter.AvailabilityClientAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

/**
 * PATRÓN FACADE (Estructural):
 * Oculta la complejidad de interactuar con el Adapter externo y el Servicio interno,
 * ofreciendo una única interfaz simplificada para el proceso de agendamiento web.
 */
@Service
@RequiredArgsConstructor
public class WebBookingFacade {

    private final AvailabilityClientAdapter availabilityAdapter;
    private final AppointmentService appointmentService;

    public String processWebBooking(WebBookingRequestDTO request) {
        
        // 1. Consultar disponibilidad real usando el Adapter
        List<LocalTime> availableSlots = availabilityAdapter.getAvailableSlots(
                request.getDoctorId(), 
                request.getAppointmentDate()
        );

        // 2. Regla de Negocio: Validar si la hora que quiere el paciente realmente está disponible
        if (!availableSlots.contains(request.getAppointmentTime())) {
            throw new RuntimeException("La franja horaria seleccionada ya no está disponible o el médico no atiende a esa hora.");
        }

        // 3. Transformar la petición para el servicio core
        AppointmentRequestDTO coreRequest = new AppointmentRequestDTO();
        coreRequest.setPatientId(request.getPatientId());
        coreRequest.setDoctorId(request.getDoctorId());
        // Aquí combinamos fecha y hora si tu entidad lo requiere, o los pasamos directos
        coreRequest.setAppointmentDate(request.getAppointmentDate().atTime(request.getAppointmentTime())); 
        coreRequest.setNotes(request.getNotes());
        coreRequest.setWhatsappNumber("WEB-BOOKING"); // Diferenciador de canal

        // 4. Delegar la creación final
        appointmentService.createAppointment(coreRequest);
        
        return "Cita agendada exitosamente a través de la plataforma web.";
    }
}