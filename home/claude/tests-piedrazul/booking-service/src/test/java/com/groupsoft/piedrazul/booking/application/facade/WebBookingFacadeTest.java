package com.groupsoft.piedrazul.booking.application.facade;

import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO;
import com.groupsoft.piedrazul.booking.application.dto.WebBookingRequestDTO;
import com.groupsoft.piedrazul.booking.application.service.AppointmentService;
import com.groupsoft.piedrazul.booking.infrastructure.adapter.AvailabilityClientAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para WebBookingFacade.
 * Cubre el patrón FACADE y RF3 (paciente agenda cita vía web).
 */
@ExtendWith(MockitoExtension.class)
class WebBookingFacadeTest {

    @Mock
    private AvailabilityClientAdapter availabilityAdapter;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private WebBookingFacade webBookingFacade;

    private WebBookingRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new WebBookingRequestDTO();
        request.setPatientId(1L);
        request.setDoctorId(10L);
        request.setAppointmentDate(LocalDate.of(2026, 6, 10));
        request.setAppointmentTime(LocalTime.of(9, 0));
        request.setNotes("Cita por portal web");
    }

    // ─────────────────────────────────────────────
    // RF3 – Agendamiento web (Facade)
    // ─────────────────────────────────────────────

    @Test
    void shouldProcessWebBookingSuccessfully() {
        // Arrange - el slot solicitado está disponible
        when(availabilityAdapter.getAvailableSlots(10L, LocalDate.of(2026, 6, 10)))
                .thenReturn(List.of(
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),   // ← el que pide el paciente
                        LocalTime.of(10, 0)));
        when(appointmentService.createAppointment(any())).thenReturn(null);

        // Act
        String result = webBookingFacade.processWebBooking(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("exitosamente"),
                "El mensaje de éxito debe confirmarse");
    }

    @Test
    void shouldThrowExceptionWhenRequestedSlotIsNotAvailable() {
        // Arrange - el slot 09:00 NO está disponible
        when(availabilityAdapter.getAvailableSlots(10L, LocalDate.of(2026, 6, 10)))
                .thenReturn(List.of(
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0)));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> webBookingFacade.processWebBooking(request));

        assertTrue(ex.getMessage().contains("no está disponible") ||
                   ex.getMessage().contains("médico no atiende"),
                "El error debe indicar que el slot no está disponible");
    }

    @Test
    void shouldCallAvailabilityAdapterBeforeCreatingAppointment() {
        // Arrange
        when(availabilityAdapter.getAvailableSlots(any(), any()))
                .thenReturn(List.of(LocalTime.of(9, 0)));
        when(appointmentService.createAppointment(any())).thenReturn(null);

        // Act
        webBookingFacade.processWebBooking(request);

        // Assert - el Facade SIEMPRE debe verificar disponibilidad antes de crear
        verify(availabilityAdapter, times(1))
                .getAvailableSlots(10L, LocalDate.of(2026, 6, 10));
        verify(appointmentService, times(1)).createAppointment(any());
    }

    @Test
    void shouldNotCreateAppointmentIfSlotUnavailable() {
        // Arrange - sin slots disponibles
        when(availabilityAdapter.getAvailableSlots(any(), any()))
                .thenReturn(List.of());

        // Act
        assertThrows(RuntimeException.class,
                () -> webBookingFacade.processWebBooking(request));

        // Assert - no debe intentar crear la cita
        verify(appointmentService, never()).createAppointment(any());
    }

    @Test
    void shouldPassCorrectDoctorIdToAdapter() {
        // Arrange
        request.setDoctorId(55L);
        when(availabilityAdapter.getAvailableSlots(55L, LocalDate.of(2026, 6, 10)))
                .thenReturn(List.of(LocalTime.of(9, 0)));
        when(appointmentService.createAppointment(any())).thenReturn(null);

        // Act
        webBookingFacade.processWebBooking(request);

        // Assert - el ID del médico se pasa correctamente al adapter
        verify(availabilityAdapter).getAvailableSlots(eq(55L), any());
    }

    @Test
    void shouldTagAppointmentAsWebBooking() {
        // Arrange
        when(availabilityAdapter.getAvailableSlots(any(), any()))
                .thenReturn(List.of(LocalTime.of(9, 0)));
        when(appointmentService.createAppointment(any())).thenReturn(null);

        // Act
        webBookingFacade.processWebBooking(request);

        // Assert - verifica que el requestDTO enviado al service tiene el tag "WEB-BOOKING"
        verify(appointmentService).createAppointment(argThat(req ->
                "WEB-BOOKING".equals(req.getWhatsappNumber())));
    }
}
