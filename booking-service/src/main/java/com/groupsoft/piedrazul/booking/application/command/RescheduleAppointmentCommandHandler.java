package com.groupsoft.piedrazul.booking.application.command;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO;
import com.groupsoft.piedrazul.booking.application.mapper.AppointmentMapper;
import com.groupsoft.piedrazul.booking.domain.Repository.AppointmentRepository;
import com.groupsoft.piedrazul.booking.domain.Repository.AppointmentRescheduleHistoryRepository;
import com.groupsoft.piedrazul.booking.domain.exception.AppointmentOverlapException;
import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentRescheduleHistory;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;
import com.groupsoft.piedrazul.booking.domain.state.AppointmentStateContext;
import com.groupsoft.piedrazul.booking.infrastructure.adapter.AvailabilityClientAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RescheduleAppointmentCommandHandler
        implements CommandHandler<RescheduleAppointmentCommand, AppointmentResponseDTO> {

    private final AppointmentRepository repository;
    private final AppointmentRescheduleHistoryRepository historyRepository;
    private final AvailabilityClientAdapter availabilityAdapter;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentStateContext stateContext;

    @Override
    public boolean supports(Command<?> command) {
        return command instanceof RescheduleAppointmentCommand;
    }

    @Override
    @Transactional
    public AppointmentResponseDTO execute(RescheduleAppointmentCommand command) {
        Appointment appointment = repository.findById(command.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        stateContext.ensureCanReschedule(appointment.getStatus());

        LocalDateTime newDate = command.getNewAppointmentDate();
        validateSlotAvailable(appointment.getDoctorId(), newDate);
        validateNoOverlap(appointment.getDoctorId(), newDate, appointment.getId());

        LocalDateTime previousDate = appointment.getAppointmentDate();

        historyRepository.save(AppointmentRescheduleHistory.builder()
                .appointmentId(appointment.getId())
                .previousDate(previousDate)
                .newDate(newDate)
                .responsibleUserId(command.getResponsibleUserId())
                .responsibleName(command.getResponsibleName())
                .changedAt(LocalDateTime.now())
                .build());

        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.RESCHEDULED);

        return appointmentMapper.toDTO(repository.save(appointment));
    }

    private void validateSlotAvailable(Long doctorId, LocalDateTime dateTime) {
        List<LocalTime> slots = availabilityAdapter.getAvailableSlots(
                doctorId, dateTime.toLocalDate());

        if (!slots.contains(dateTime.toLocalTime())) {
            throw new IllegalArgumentException(
                    "La nueva franja no esta disponible segun la configuracion del medico.");
        }
    }

    private void validateNoOverlap(Long doctorId, LocalDateTime dateTime, Long excludeId) {
        boolean occupied = repository.existsByDoctorIdAndAppointmentDateAndIdNot(
                doctorId, dateTime, excludeId);
        if (occupied) {
            throw new AppointmentOverlapException(
                    "El doctor ya tiene una cita reservada en esa fecha y hora.");
        }
    }
}
