package com.groupsoft.piedrazul.booking.application.service;

import com.groupsoft.piedrazul.booking.application.command.CommandInvoker;
import com.groupsoft.piedrazul.booking.application.command.RescheduleAppointmentCommand;
import com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO;
import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO;
import com.groupsoft.piedrazul.booking.application.dto.RescheduleHistoryDTO;
import com.groupsoft.piedrazul.booking.application.dto.RescheduleRequestDTO;
import com.groupsoft.piedrazul.booking.application.mapper.AppointmentMapper;
import com.groupsoft.piedrazul.booking.domain.Repository.AppointmentRepository;
import com.groupsoft.piedrazul.booking.domain.Repository.AppointmentRescheduleHistoryRepository;
import com.groupsoft.piedrazul.booking.domain.event.AppointmentCreatedEvent;
import com.groupsoft.piedrazul.booking.domain.exception.AppointmentOverlapException;
import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentRescheduleHistoryRepository historyRepository;
    private final AppointmentEventPublisher eventPublisher;
    private final AppointmentMapper appointmentMapper;
    private final CommandInvoker commandInvoker;

    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
        validateNoOverlap(request.getDoctorId(), request.getAppointmentDate(), null);

        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .appointmentDate(request.getAppointmentDate())
                .whatsappNumber(request.getWhatsappNumber())
                .notes(request.getNotes())
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment savedAppointment = repository.save(appointment);

        eventPublisher.publishAppointmentCreatedEvent(new AppointmentCreatedEvent(
                savedAppointment.getId(),
                savedAppointment.getPatientId(),
                savedAppointment.getDoctorId(),
                savedAppointment.getAppointmentDate(),
                savedAppointment.getWhatsappNumber()
        ));

        return appointmentMapper.toDTO(savedAppointment);
    }

    public List<AppointmentResponseDTO> getAppointmentsByDoctorAndDate(
            Long doctorId, LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        return repository
                .findByDoctorIdAndAppointmentDateBetween(doctorId, start, end)
                .stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<LocalTime> getOccupiedTimes(Long doctorId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        return repository
                .findByDoctorIdAndAppointmentDateBetween(doctorId, start, end)
                .stream()
                .map(a -> a.getAppointmentDate().toLocalTime())
                .distinct()
                .collect(Collectors.toList());
    }

    public AppointmentResponseDTO rescheduleAppointment(
            Long appointmentId, RescheduleRequestDTO request) {

        return commandInvoker.execute(RescheduleAppointmentCommand.builder()
                .appointmentId(appointmentId)
                .newAppointmentDate(request.getNewAppointmentDate())
                .responsibleUserId(request.getResponsibleUserId())
                .responsibleName(request.getResponsibleName())
                .build());
    }

    public List<RescheduleHistoryDTO> getRescheduleHistory(Long appointmentId) {
        return historyRepository.findByAppointmentIdOrderByChangedAtDesc(appointmentId)
                .stream()
                .map(h -> RescheduleHistoryDTO.builder()
                        .id(h.getId())
                        .appointmentId(h.getAppointmentId())
                        .previousDate(h.getPreviousDate())
                        .newDate(h.getNewDate())
                        .responsibleUserId(h.getResponsibleUserId())
                        .responsibleName(h.getResponsibleName())
                        .changedAt(h.getChangedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public String exportAppointmentsToCsv(Long doctorId, LocalDate date) {
        List<AppointmentResponseDTO> appointments =
                getAppointmentsByDoctorAndDate(doctorId, date);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Fecha,Hora,Paciente,Documento,Celular,Estado,Notas\n");

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        for (AppointmentResponseDTO apt : appointments) {
            csv.append(apt.getId()).append(",");
            csv.append(apt.getAppointmentDate().format(dateFmt)).append(",");
            csv.append(apt.getAppointmentDate().format(timeFmt)).append(",");
            csv.append(escapeCsv(apt.getPatientName())).append(",");
            csv.append(escapeCsv(apt.getPatientDocument())).append(",");
            csv.append(escapeCsv(apt.getWhatsappNumber())).append(",");
            csv.append(apt.getStatus()).append(",");
            csv.append(escapeCsv(apt.getNotes())).append("\n");
        }
        return csv.toString();
    }

    private void validateNoOverlap(
            Long doctorId, LocalDateTime dateTime, Long excludeAppointmentId) {

        boolean occupied = excludeAppointmentId == null
                ? repository.existsByDoctorIdAndAppointmentDate(doctorId, dateTime)
                : repository.existsByDoctorIdAndAppointmentDateAndIdNot(
                        doctorId, dateTime, excludeAppointmentId);

        if (occupied) {
            throw new AppointmentOverlapException(
                    "El doctor ya tiene una cita reservada en esa fecha y hora.");
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
