package com.groupsoft.piedrazul.booking.application.facade;
// Paquete donde se ubican las pruebas unitarias de la capa de aplicación (Facade) 
// dentro del bounded context Booking.

// ─────────────────────────────────────────────
// Imports de clases propias del dominio y aplicación
// ─────────────────────────────────────────────

import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO; 
// DTO de respuesta para citas (aunque en este test no se usa directamente).

import com.groupsoft.piedrazul.booking.application.dto.WebBookingRequestDTO; 
// DTO que representa la solicitud de agendamiento vía portal web.

import com.groupsoft.piedrazul.booking.application.service.AppointmentService; 
// Servicio de aplicación encargado de la lógica de creación de citas.

import com.groupsoft.piedrazul.booking.infrastructure.adapter.AvailabilityClientAdapter; 
// Adapter para consultar disponibilidad de médicos en el microservicio de disponibilidad.

// ─────────────────────────────────────────────
// Imports de JUnit 5 para pruebas unitarias
// ─────────────────────────────────────────────

import org.junit.jupiter.api.BeforeEach; 
// Anotación para ejecutar un método antes de cada prueba.

import org.junit.jupiter.api.Test; 
// Anotación para marcar un método como caso de prueba.

import org.junit.jupiter.api.extension.ExtendWith; 
// Permite extender el comportamiento de JUnit con extensiones (ej. Mockito).

// ─────────────────────────────────────────────
// Imports de Mockito para simulación de dependencias
// ─────────────────────────────────────────────

import org.mockito.InjectMocks; 
// Inyecta los mocks en la clase bajo prueba (SUT).

import org.mockito.Mock; 
// Marca un objeto como mock (dependencia simulada).

import org.mockito.junit.jupiter.MockitoExtension; 
// Extensión de Mockito para integrarse con JUnit 5.

// ─────────────────────────────────────────────
// Imports de Java estándar para fechas y colecciones
// ─────────────────────────────────────────────

import java.time.LocalDate; 
// Representa una fecha sin zona horaria.

import java.time.LocalTime; 
// Representa una hora del día sin fecha.

import java.util.List; 
// Colección de tipo lista para manejar slots disponibles.

// ─────────────────────────────────────────────
// Imports estáticos para simplificar llamadas en pruebas
// ─────────────────────────────────────────────

import static org.junit.jupiter.api.Assertions.*; 
// Métodos de aserción de JUnit (assertEquals, assertTrue, etc.).

import static org.mockito.ArgumentMatchers.any; 
// Matcher de Mockito para aceptar cualquier argumento en un mock.

import static org.mockito.Mockito.*; 
// Métodos de verificación y configuración de Mockito (when, verify, times, never, etc.).


/**
 * Pruebas unitarias para WebBookingFacade.
 * Cubre el patrón FACADE y RF3 (paciente agenda cita vía web).
 */
@ExtendWith(MockitoExtension.class)
class WebBookingFacadeTest {

    @Mock
    private AvailabilityClientAdapter availabilityAdapter; // Adapter para consultar disponibilidad

    @Mock
    private AppointmentService appointmentService; // Servicio de creación de citas

    @InjectMocks
    private WebBookingFacade webBookingFacade; // Facade bajo prueba (SUT)

    private WebBookingRequestDTO request; // DTO de entrada para pruebas

    @BeforeEach
    void setUp() {
        // Construcción de un request de prueba
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
