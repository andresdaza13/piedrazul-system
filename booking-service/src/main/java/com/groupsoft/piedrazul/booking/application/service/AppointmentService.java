package com.groupsoft.piedrazul.booking.application.service;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO;
import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO;
import com.groupsoft.piedrazul.booking.domain.event.AppointmentCreatedEvent;
import com.groupsoft.piedrazul.booking.domain.exception.AppointmentOverlapException;
import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;
import com.groupsoft.piedrazul.booking.domain.Repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// @Service: indica que esta clase es un componente de la capa de servicio.
// @RequiredArgsConstructor: genera automáticamente el constructor con los atributos finales.
@Service
@RequiredArgsConstructor
public class AppointmentService {

    // Repositorio para acceder a la base de datos de citas.
    private final AppointmentRepository repository;

    // Publicador de eventos: emite eventos de creación de citas hacia RabbitMQ.
    private final AppointmentEventPublisher eventPublisher;

    // Requisito 2 - Crear cita desde WhatsApp
    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {

        // Validación: verificar si ya existe una cita en la misma fecha/hora para el mismo doctor.
        boolean isOccupied = repository.existsByDoctorIdAndAppointmentDate(
                request.getDoctorId(),
                request.getAppointmentDate()
        );

        if (isOccupied) {
            // Si hay conflicto, se lanza una excepción personalizada.
            throw new AppointmentOverlapException(
                "El doctor ya tiene una cita reservada en esa fecha y hora."
            );
        }

        // Construcción del objeto Appointment usando el patrón Builder.
        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .appointmentDate(request.getAppointmentDate())
                .whatsappNumber(request.getWhatsappNumber())
                .notes(request.getNotes())
                .status(AppointmentStatus.PENDING) // Estado inicial de la cita.
                .build();

        // Persistencia en la base de datos.
        Appointment savedAppointment = repository.save(appointment);

        // Creación del evento de cita creada.
        AppointmentCreatedEvent event = new AppointmentCreatedEvent(
                savedAppointment.getId(),
                savedAppointment.getPatientId(),
                savedAppointment.getDoctorId(),
                savedAppointment.getAppointmentDate(),
                savedAppointment.getWhatsappNumber()
        );

        // Publicación del evento en RabbitMQ (patrón Observer).
        eventPublisher.publishAppointmentCreatedEvent(event);

        // Transformación a DTO de respuesta.
        return toDTO(savedAppointment);
    }

    // Requisito 1 - Listar citas de un médico en una fecha.
    public List<AppointmentResponseDTO> getAppointmentsByDoctorAndDate(
            Long doctorId, LocalDate date) {

        // Definir rango de tiempo (inicio y fin del día).
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        // Consultar citas en el repositorio y mapearlas a DTOs.
        return repository
                .findByDoctorIdAndAppointmentDateBetween(doctorId, start, end)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Método auxiliar para transformar un objeto Appointment en un DTO de respuesta.
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
