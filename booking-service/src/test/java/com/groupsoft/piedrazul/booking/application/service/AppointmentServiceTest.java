package com.groupsoft.piedrazul.booking.application.service;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO;
import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO;
import com.groupsoft.piedrazul.booking.domain.Repository.AppointmentRepository;
import com.groupsoft.piedrazul.booking.domain.exception.AppointmentOverlapException;
import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private AppointmentEventPublisher eventPublisher;

    @InjectMocks
    private AppointmentService appointmentService;

    private AppointmentRequestDTO requestDTO;
    private Appointment appointment;

    @BeforeEach
    void setUp() {

        requestDTO = new AppointmentRequestDTO();
        requestDTO.setPatientId(1L);
        requestDTO.setDoctorId(10L);
        requestDTO.setAppointmentDate(LocalDateTime.of(2026,5,28,10,0));
        requestDTO.setWhatsappNumber("3001234567");
        requestDTO.setNotes("Consulta general");

        appointment = Appointment.builder()
                .id(100L)
                .patientId(1L)
                .doctorId(10L)
                .appointmentDate(requestDTO.getAppointmentDate())
                .status(AppointmentStatus.PENDING)
                .whatsappNumber("3001234567")
                .notes("Consulta general")
                .build();
    }

    // TEST: crea una cita correctamente
    @Test
    void shouldCreateAppointmentSuccessfully() {

        when(repository.existsByDoctorIdAndAppointmentDate(
                requestDTO.getDoctorId(),
                requestDTO.getAppointmentDate()
        )).thenReturn(false);

        when(repository.save(any(Appointment.class)))
                .thenReturn(appointment);

        AppointmentResponseDTO response =
                appointmentService.createAppointment(requestDTO);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(AppointmentStatus.PENDING, response.getStatus());

        verify(repository).save(any(Appointment.class));
        verify(eventPublisher).publishAppointmentCreatedEvent(any());
    }

    // TEST: lanza una excepción cuando hay superposición de citas
    @Test
    void shouldThrowExceptionWhenAppointmentOverlaps() {

        when(repository.existsByDoctorIdAndAppointmentDate(
                requestDTO.getDoctorId(),
                requestDTO.getAppointmentDate()
        )).thenReturn(true);

        assertThrows(
                AppointmentOverlapException.class,
                () -> appointmentService.createAppointment(requestDTO)
        );

        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishAppointmentCreatedEvent(any());
    }

    // TEST: devuelve citas por médico y fecha
    @Test
    void shouldReturnAppointmentsByDoctorAndDate() {

        LocalDate date = LocalDate.of(2026,5,28);

        when(repository.findByDoctorIdAndAppointmentDateBetween(
                eq(10L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(appointment));

        List<AppointmentResponseDTO> result =
                appointmentService.getAppointmentsByDoctorAndDate(10L, date);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPatientId());
    }

    // TEST: devuelve una lista vacía cuando no existen citas
    @Test
    void shouldReturnEmptyListWhenNoAppointmentsExist() {

        LocalDate date = LocalDate.of(2026,5,28);

        when(repository.findByDoctorIdAndAppointmentDateBetween(
                eq(10L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of());

        List<AppointmentResponseDTO> result =
                appointmentService.getAppointmentsByDoctorAndDate(10L, date);

        assertTrue(result.isEmpty());
    }

    // TEST: publica un evento después de guardar la cita
    @Test
    void shouldPublishEventAfterSavingAppointment() {

        when(repository.existsByDoctorIdAndAppointmentDate(
                anyLong(),
                any(LocalDateTime.class)
        )).thenReturn(false);

        when(repository.save(any(Appointment.class)))
                .thenReturn(appointment);

        appointmentService.createAppointment(requestDTO);

        verify(eventPublisher, times(1))
                .publishAppointmentCreatedEvent(any());
    }
}