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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para AppointmentService.
 * Cubre RF1 (listar citas) y RF2 (crear cita desde WhatsApp),
 * incluyendo la validación de solapamiento y la publicación de eventos.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private AppointmentEventPublisher eventPublisher;

    @InjectMocks
    private AppointmentService appointmentService;

    private AppointmentRequestDTO requestDTO;
    private Appointment savedAppointment;

    @BeforeEach
    void setUp() {
        requestDTO = new AppointmentRequestDTO();
        requestDTO.setPatientId(1L);
        requestDTO.setDoctorId(10L);
        requestDTO.setAppointmentDate(LocalDateTime.of(2026, 6, 10, 9, 0));
        requestDTO.setWhatsappNumber("+573001234567");
        requestDTO.setNotes("Consulta general");

        savedAppointment = Appointment.builder()
                .id(100L)
                .patientId(1L)
                .doctorId(10L)
                .appointmentDate(LocalDateTime.of(2026, 6, 10, 9, 0))
                .status(AppointmentStatus.PENDING)
                .whatsappNumber("+573001234567")
                .notes("Consulta general")
                .build();
    }

    // ─────────────────────────────────────────────
    // RF2 – Crear cita desde WhatsApp
    // ─────────────────────────────────────────────

    @Test
    void shouldCreateAppointmentSuccessfullyWhenSlotIsAvailable() {
        // Arrange
        when(repository.existsByDoctorIdAndAppointmentDate(10L,
                LocalDateTime.of(2026, 6, 10, 9, 0)))
                .thenReturn(false);
        when(repository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // Act
        AppointmentResponseDTO response = appointmentService.createAppointment(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(AppointmentStatus.PENDING, response.getStatus());
        verify(repository, times(1)).save(any(Appointment.class));
    }

    @Test
    void shouldThrowAppointmentOverlapExceptionWhenSlotIsOccupied() {
        // Arrange - el slot ya está ocupado
        when(repository.existsByDoctorIdAndAppointmentDate(any(), any()))
                .thenReturn(true);

        // Act & Assert
        AppointmentOverlapException ex = assertThrows(
                AppointmentOverlapException.class,
                () -> appointmentService.createAppointment(requestDTO));

        assertTrue(ex.getMessage().contains("ya tiene una cita"),
                "El mensaje debe indicar solapamiento");
        verify(repository, never()).save(any(Appointment.class));
    }

    @Test
    void newAppointmentShouldAlwaysStartWithStatusPending() {
        // Arrange
        when(repository.existsByDoctorIdAndAppointmentDate(any(), any()))
                .thenReturn(false);
        when(repository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // Act
        AppointmentResponseDTO response = appointmentService.createAppointment(requestDTO);

        // Assert
        assertEquals(AppointmentStatus.PENDING, response.getStatus(),
                "Toda cita nueva debe iniciar en estado PENDING");
    }

    @Test
    void shouldPublishAppointmentCreatedEventAfterSave() {
        // Arrange
        when(repository.existsByDoctorIdAndAppointmentDate(any(), any()))
                .thenReturn(false);
        when(repository.save(any(Appointment.class))).thenReturn(savedAppointment);
        doNothing().when(eventPublisher).publishAppointmentCreatedEvent(any());

        // Act
        appointmentService.createAppointment(requestDTO);

        // Assert - el evento siempre debe publicarse tras guardar
        verify(eventPublisher, times(1)).publishAppointmentCreatedEvent(any());
    }

    @Test
    void shouldNotPublishEventIfSaveFails() {
        // Arrange
        when(repository.existsByDoctorIdAndAppointmentDate(any(), any()))
                .thenReturn(false);
        when(repository.save(any(Appointment.class)))
                .thenThrow(new RuntimeException("Error de BD"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> appointmentService.createAppointment(requestDTO));

        verify(eventPublisher, never()).publishAppointmentCreatedEvent(any());
    }

    // ─────────────────────────────────────────────
    // RF1 – Listar citas de un médico en una fecha
    // ─────────────────────────────────────────────

    @Test
    void shouldReturnAppointmentsForDoctorOnGivenDate() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 6, 10);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.atTime(23, 59, 59);

        when(repository.findByDoctorIdAndAppointmentDateBetween(10L, start, end))
                .thenReturn(List.of(savedAppointment));

        // Act
        List<AppointmentResponseDTO> results =
                appointmentService.getAppointmentsByDoctorAndDate(10L, date);

        // Assert
        assertEquals(1, results.size());
        assertEquals(10L, results.get(0).getDoctorId());
    }

    @Test
    void shouldReturnEmptyListWhenNoCitasForDoctor() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 6, 10);
        when(repository.findByDoctorIdAndAppointmentDateBetween(any(), any(), any()))
                .thenReturn(List.of());

        // Act
        List<AppointmentResponseDTO> results =
                appointmentService.getAppointmentsByDoctorAndDate(99L, date);

        // Assert
        assertTrue(results.isEmpty(),
                "Debe retornar lista vacía si no hay citas para ese médico y fecha");
    }

    @Test
    void shouldQueryCorrectDateRangeForGivenDate() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 6, 15);
        LocalDateTime expectedStart = LocalDateTime.of(2026, 6, 15, 0, 0, 0);
        LocalDateTime expectedEnd   = LocalDateTime.of(2026, 6, 15, 23, 59, 59);

        when(repository.findByDoctorIdAndAppointmentDateBetween(
                eq(10L), eq(expectedStart), eq(expectedEnd)))
                .thenReturn(List.of());

        // Act
        appointmentService.getAppointmentsByDoctorAndDate(10L, date);

        // Assert - verificamos que el rango de fechas sea exactamente el del día
        verify(repository).findByDoctorIdAndAppointmentDateBetween(
                eq(10L), eq(expectedStart), eq(expectedEnd));
    }
}
