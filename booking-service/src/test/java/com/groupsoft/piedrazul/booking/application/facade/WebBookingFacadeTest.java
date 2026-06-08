package com.groupsoft.piedrazul.booking.application.facade;

import com.groupsoft.piedrazul.booking.application.dto.WebBookingRequestDTO;
import com.groupsoft.piedrazul.booking.application.service.AppointmentService;
import com.groupsoft.piedrazul.booking.infrastructure.adapter.AvailabilityClientAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class): habilita Mockito en JUnit 5 para pruebas con mocks.
@ExtendWith(MockitoExtension.class)
class WebBookingFacadeTest {

    // Se simulan dependencias externas con @Mock.
    @Mock
    private AvailabilityClientAdapter availabilityAdapter;

    @Mock
    private AppointmentService appointmentService;

    // @InjectMocks: crea una instancia real de WebBookingFacade e inyecta los mocks.
    @InjectMocks
    private WebBookingFacade webBookingFacade;

    private WebBookingRequestDTO request;

    // Configuración inicial antes de cada prueba.
    @BeforeEach
    void setUp() {
        request = new WebBookingRequestDTO();
        request.setPatientId(1L);
        request.setDoctorId(10L);
        request.setAppointmentDate(LocalDate.of(2026,5,28));
        request.setAppointmentTime(LocalTime.of(10,0));
        request.setNotes("Consulta web");
    }

    // ✅ Caso exitoso: la franja horaria está disponible.
    @Test
    void shouldProcessWebBookingSuccessfully() {
        // Simula que el adapter devuelve la hora solicitada como disponible.
        when(availabilityAdapter.getAvailableSlots(
                request.getDoctorId(),
                request.getAppointmentDate()
        )).thenReturn(List.of(LocalTime.of(10,0)));

        // Ejecuta el método de la fachada.
        String response = webBookingFacade.processWebBooking(request);

        // Verifica que el mensaje de éxito sea el esperado.
        assertEquals(
                "Cita agendada exitosamente a través de la plataforma web.",
                response
        );

        // Verifica que se haya llamado al servicio para crear la cita.
        verify(appointmentService).createAppointment(any());
    }

    // ❌ Caso de error: la franja horaria no está disponible.
    @Test
    void shouldThrowExceptionWhenTimeSlotIsUnavailable() {
        // Simula que el adapter devuelve otra hora distinta.
        when(availabilityAdapter.getAvailableSlots(
                request.getDoctorId(),
                request.getAppointmentDate()
        )).thenReturn(List.of(LocalTime.of(9,0)));

        // Verifica que se lance una excepción.
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> webBookingFacade.processWebBooking(request)
        );

        // El mensaje debe indicar que la franja ya no está disponible.
        assertTrue(exception.getMessage().contains("ya no está disponible"));

        // Verifica que NO se haya llamado al servicio de creación.
        verify(appointmentService, never()).createAppointment(any());
    }

    // 🔄 Caso de transformación: validar que el DTO web se convierte correctamente en DTO core.
    @Test
    void shouldTransformRequestCorrectlyBeforeCallingService() {
        // Simula que la hora solicitada está disponible.
        when(availabilityAdapter.getAvailableSlots(
                anyLong(),
                any()
        )).thenReturn(List.of(LocalTime.of(10,0)));

        // Ejecuta el método de la fachada.
        webBookingFacade.processWebBooking(request);

        // Captura el argumento enviado al servicio.
        ArgumentCaptor<com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO>
                captor = ArgumentCaptor.forClass(
                com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO.class
        );

        verify(appointmentService).createAppointment(captor.capture());

        var captured = captor.getValue();

        // Verifica que los datos se transformaron correctamente.
        assertEquals(1L, captured.getPatientId());
        assertEquals(10L, captured.getDoctorId());
        assertEquals("WEB-BOOKING", captured.getWhatsappNumber());
        assertEquals(
                request.getAppointmentDate().atTime(request.getAppointmentTime()),
                captured.getAppointmentDate()
        );
    }
}
