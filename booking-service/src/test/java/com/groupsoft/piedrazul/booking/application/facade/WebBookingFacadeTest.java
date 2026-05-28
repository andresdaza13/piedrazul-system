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
        request.setAppointmentDate(LocalDate.of(2026,5,28));
        request.setAppointmentTime(LocalTime.of(10,0));
        request.setNotes("Consulta web");
    }

    @Test
    void shouldProcessWebBookingSuccessfully() {

        when(availabilityAdapter.getAvailableSlots(
                request.getDoctorId(),
                request.getAppointmentDate()
        )).thenReturn(List.of(LocalTime.of(10,0)));

        String response = webBookingFacade.processWebBooking(request);

        assertEquals(
                "Cita agendada exitosamente a través de la plataforma web.",
                response
        );

        verify(appointmentService).createAppointment(any());
    }

    @Test
    void shouldThrowExceptionWhenTimeSlotIsUnavailable() {

        when(availabilityAdapter.getAvailableSlots(
                request.getDoctorId(),
                request.getAppointmentDate()
        )).thenReturn(List.of(LocalTime.of(9,0)));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> webBookingFacade.processWebBooking(request)
        );

        assertTrue(exception.getMessage()
                .contains("ya no está disponible"));

        verify(appointmentService, never())
                .createAppointment(any());
    }

    @Test
    void shouldTransformRequestCorrectlyBeforeCallingService() {

        when(availabilityAdapter.getAvailableSlots(
                anyLong(),
                any()
        )).thenReturn(List.of(LocalTime.of(10,0)));

        webBookingFacade.processWebBooking(request);

        ArgumentCaptor<com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO>
                captor = ArgumentCaptor.forClass(
                com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO.class
        );

        verify(appointmentService).createAppointment(captor.capture());

        var captured = captor.getValue();

        assertEquals(1L, captured.getPatientId());
        assertEquals(10L, captured.getDoctorId());
        assertEquals("WEB-BOOKING", captured.getWhatsappNumber());
        assertEquals(
                request.getAppointmentDate().atTime(request.getAppointmentTime()),
                captured.getAppointmentDate()
        );
    }
}