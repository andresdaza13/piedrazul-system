package com.groupsoft.piedrazul.booking.application.command;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO;
import com.groupsoft.piedrazul.booking.application.mapper.AppointmentMapper;
import com.groupsoft.piedrazul.booking.domain.Repository.AppointmentRepository;
import com.groupsoft.piedrazul.booking.domain.Repository.AppointmentRescheduleHistoryRepository;
import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;
import com.groupsoft.piedrazul.booking.domain.state.AppointmentStateContext;
import com.groupsoft.piedrazul.booking.infrastructure.adapter.AvailabilityClientAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RescheduleAppointmentCommandHandlerTest {

    @Mock private AppointmentRepository repository;
    @Mock private AppointmentRescheduleHistoryRepository historyRepository;
    @Mock private AvailabilityClientAdapter availabilityAdapter;
    @Mock private AppointmentMapper appointmentMapper;
    @Mock private AppointmentStateContext stateContext;

    @InjectMocks
    private RescheduleAppointmentCommandHandler handler;

    @Test
    void shouldSupportRescheduleCommand() {
        assertTrue(handler.supports(RescheduleAppointmentCommand.builder()
                .appointmentId(1L)
                .newAppointmentDate(LocalDateTime.now().plusDays(1))
                .responsibleUserId(1L)
                .responsibleName("Dr. Test")
                .build()));
    }

    @Test
    void shouldExecuteRescheduleSuccessfully() {
        Appointment appointment = Appointment.builder()
                .id(10L)
                .patientId(1L)
                .doctorId(5L)
                .appointmentDate(LocalDateTime.of(2026, 6, 1, 9, 0))
                .status(AppointmentStatus.PENDING)
                .build();

        LocalDateTime newDate = LocalDateTime.of(2026, 6, 2, 10, 0);

        when(repository.findById(10L)).thenReturn(Optional.of(appointment));
        when(availabilityAdapter.getAvailableSlots(5L, newDate.toLocalDate()))
                .thenReturn(List.of(LocalTime.of(10, 0)));
        when(repository.existsByDoctorIdAndAppointmentDateAndIdNot(5L, newDate, 10L))
                .thenReturn(false);
        when(repository.save(any())).thenReturn(appointment);
        when(appointmentMapper.toDTO(any())).thenReturn(AppointmentResponseDTO.builder().id(10L).build());

        RescheduleAppointmentCommand command = RescheduleAppointmentCommand.builder()
                .appointmentId(10L)
                .newAppointmentDate(newDate)
                .responsibleUserId(2L)
                .responsibleName("Agendador Piedrazul")
                .build();

        AppointmentResponseDTO result = handler.execute(command);

        assertNotNull(result);
        verify(historyRepository).save(any());
        verify(stateContext).ensureCanReschedule(AppointmentStatus.PENDING);
    }
}
