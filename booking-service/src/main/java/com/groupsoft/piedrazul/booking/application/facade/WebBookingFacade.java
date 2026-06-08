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
 * Esta clase oculta la complejidad de interactuar con el Adapter externo (AvailabilityClientAdapter)
 * y el Servicio interno (AppointmentService), ofreciendo una única interfaz simplificada
 * para el proceso de agendamiento web.
 */
@Service
@RequiredArgsConstructor
public class WebBookingFacade {

    // Adapter para consultar disponibilidad en el microservicio availability-service.
    private final AvailabilityClientAdapter availabilityAdapter;

    // Servicio core que gestiona la creación de citas.
    private final AppointmentService appointmentService;

    /**
     * Método principal que procesa el agendamiento web.
     * Orquesta la consulta de disponibilidad, validación de reglas de negocio
     * y creación final de la cita.
     *
     * @param request DTO con los datos de la cita solicitada desde la web.
     * @return Mensaje de confirmación de la operación.
     */
    public String processWebBooking(WebBookingRequestDTO request) {
        
        // 1. Consultar disponibilidad real usando el Adapter.
        List<LocalTime> availableSlots = availabilityAdapter.getAvailableSlots(
                request.getDoctorId(), 
                request.getAppointmentDate()
        );

        // 2. Regla de negocio: validar si la hora seleccionada está realmente disponible.
        if (!availableSlots.contains(request.getAppointmentTime())) {
            throw new RuntimeException("La franja horaria seleccionada ya no está disponible o el médico no atiende a esa hora.");
        }

        // 3. Transformar la petición web en un DTO core para el servicio.
        AppointmentRequestDTO coreRequest = new AppointmentRequestDTO();
        coreRequest.setPatientId(request.getPatientId());
        coreRequest.setDoctorId(request.getDoctorId());
        // Combina fecha y hora en un LocalDateTime.
        coreRequest.setAppointmentDate(request.getAppointmentDate().atTime(request.getAppointmentTime())); 
        coreRequest.setNotes(request.getNotes());
        coreRequest.setWhatsappNumber("WEB-BOOKING"); // Diferenciador de canal.

        // 4. Delegar la creación final al AppointmentService.
        appointmentService.createAppointment(coreRequest);
        
        return "Cita agendada exitosamente a través de la plataforma web.";
    }
}
