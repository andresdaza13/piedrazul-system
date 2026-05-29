package com.groupsoft.piedrazul.booking.application.mapper;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO;
import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.infrastructure.adapter.UserClientAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppointmentMapper {

    private final UserClientAdapter userClientAdapter;

    public AppointmentResponseDTO toDTO(Appointment appointment) {
        String patientName = null;
        String patientDocument = null;

        var userOpt = userClientAdapter.getUserById(appointment.getPatientId());
        if (userOpt.isPresent()) {
            Map<String, Object> user = userOpt.get();
            patientName = (String) user.get("fullName");
            patientDocument = (String) user.get("documentNumber");
        }

        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .patientName(patientName)
                .patientDocument(patientDocument)
                .doctorId(appointment.getDoctorId())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .whatsappNumber(appointment.getWhatsappNumber())
                .notes(appointment.getNotes())
                .build();
    }
}
