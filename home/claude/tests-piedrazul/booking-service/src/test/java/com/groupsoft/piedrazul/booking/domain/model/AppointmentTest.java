package com.groupsoft.piedrazul.booking.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad Appointment y el enum AppointmentStatus.
 */
class AppointmentTest {

    @Test
    void builderShouldCreateAppointmentWithPendingStatus() {
        Appointment appt = Appointment.builder()
                .patientId(1L)
                .doctorId(2L)
                .appointmentDate(LocalDateTime.of(2026, 6, 10, 9, 0))
                .status(AppointmentStatus.PENDING)
                .whatsappNumber("+573001234567")
                .notes("Primera consulta")
                .build();

        assertEquals(1L, appt.getPatientId());
        assertEquals(2L, appt.getDoctorId());
        assertEquals(AppointmentStatus.PENDING, appt.getStatus());
        assertEquals("+573001234567", appt.getWhatsappNumber());
        assertEquals("Primera consulta", appt.getNotes());
    }

    @Test
    void shouldAllowStatusTransitionFromPendingToCancelled() {
        Appointment appt = Appointment.builder()
                .patientId(1L)
                .doctorId(2L)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .status(AppointmentStatus.PENDING)
                .whatsappNumber("300")
                .build();

        appt.setStatus(AppointmentStatus.CANCELLED);

        assertEquals(AppointmentStatus.CANCELLED, appt.getStatus());
    }

    @Test
    void shouldAllowStatusTransitionFromPendingToCompleted() {
        Appointment appt = Appointment.builder()
                .patientId(3L)
                .doctorId(4L)
                .appointmentDate(LocalDateTime.now().minusDays(1))
                .status(AppointmentStatus.PENDING)
                .whatsappNumber("301")
                .build();

        appt.setStatus(AppointmentStatus.COMPLETED);

        assertEquals(AppointmentStatus.COMPLETED, appt.getStatus());
    }

    @Test
    void appointmentStatusShouldHaveFourValues() {
        assertNotNull(AppointmentStatus.PENDING);
        assertNotNull(AppointmentStatus.CONFIRMED);
        assertNotNull(AppointmentStatus.CANCELLED);
        assertNotNull(AppointmentStatus.COMPLETED);
        assertEquals(4, AppointmentStatus.values().length);
    }

    @Test
    void shouldAllowNullWhatsappNumberForWebBookings() {
        // Las citas web usan "WEB-BOOKING" como diferenciador
        Appointment appt = Appointment.builder()
                .patientId(5L)
                .doctorId(6L)
                .appointmentDate(LocalDateTime.of(2026, 7, 1, 10, 0))
                .status(AppointmentStatus.PENDING)
                .whatsappNumber("WEB-BOOKING")
                .build();

        assertEquals("WEB-BOOKING", appt.getWhatsappNumber());
    }

    @Test
    void appointmentDateShouldBeStoredCorrectly() {
        LocalDateTime expectedDate = LocalDateTime.of(2026, 6, 15, 14, 30);

        Appointment appt = Appointment.builder()
                .patientId(7L)
                .doctorId(8L)
                .appointmentDate(expectedDate)
                .status(AppointmentStatus.PENDING)
                .whatsappNumber("302")
                .build();

        assertEquals(expectedDate, appt.getAppointmentDate());
    }
}
