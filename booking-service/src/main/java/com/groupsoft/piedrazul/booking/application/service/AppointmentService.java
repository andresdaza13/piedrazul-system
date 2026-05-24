package com.groupsoft.piedrazul.booking.application.service;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO;
import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO;
import com.groupsoft.piedrazul.booking.domain.event.AppointmentCreatedEvent;
import com.groupsoft.piedrazul.booking.domain.exception.AppointmentOverlapException;
import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;
import com.groupsoft.piedrazul.booking.infrastructure.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentEventPublisher eventPublisher;

    // Requisito 2 - Crear cita desde WhatsApp
    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {

        boolean isOccupied = repository.existsByDoctorIdAndAppointmentDate(
                request.getDoctorId(),
                request.getAppointmentDate()
        );

        if (isOccupied) {
            throw new AppointmentOverlapException(
                "El doctor ya tiene una cita reservada en esa fecha y hora."
            );
        }

        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .appointmentDate(request.getAppointmentDate())
                .whatsappNumber(request.getWhatsappNumber())
                .notes(request.getNotes())
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment savedAppointment = repository.save(appointment);

        AppointmentCreatedEvent event = new AppointmentCreatedEvent(
                savedAppointment.getId(),
                savedAppointment.getPatientId(),
                savedAppointment.getDoctorId(),
                savedAppointment.getAppointmentDate(),
                savedAppointment.getWhatsappNumber()
        );
        eventPublisher.publishAppointmentCreatedEvent(event);

        return toDTO(savedAppointment);
    }

    // Requisito 1 - Listar citas de un medico en una fecha
    public List<AppointmentResponseDTO> getAppointmentsByDoctorAndDate(
            Long doctorId, LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        return repository
                .findByDoctorIdAndAppointmentDateBetween(doctorId, start, end)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AppointmentResponseDTO toDTO(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .whatsappNumber(appointment.getWhatsappNumber())
                .notes(appointment.getNotes())
                .build();
    }
}