package com.groupsoft.piedrazul.booking.domain.model;
// Paquete de pruebas unitarias para las entidades del dominio Booking

import org.junit.jupiter.api.Test; 
// Anotación de JUnit 5 para definir métodos de prueba

import java.time.LocalDateTime; 
// Clase estándar de Java para manejar fecha y hora

import static org.junit.jupiter.api.Assertions.*; 
// Métodos de aserción de JUnit (assertEquals, assertTrue, assertNotNull, etc.)

/**
 * Pruebas unitarias para la entidad Appointment y el enum AppointmentStatus.
 */
class AppointmentTest {

    @Test
    void builderShouldCreateAppointmentWithPendingStatus() {
        // Construcción de cita con estado inicial PENDING
        Appointment appt = Appointment.builder()
                .patientId(1L)
                .doctorId(2L)
                .appointmentDate(LocalDateTime.of(2026, 6, 10, 9, 0))
                .status(AppointmentStatus.PENDING)
                .whatsappNumber("+573001234567")
                .notes("Primera consulta")
                .build();

        // Validaciones de los campos
        assertEquals(1L, appt.getPatientId());
        assertEquals(2L, appt.getDoctorId());
        assertEquals(AppointmentStatus.PENDING, appt.getStatus());
        assertEquals("+573001234567", appt.getWhatsappNumber());
        assertEquals("Primera consulta", appt.getNotes());
    }

    @Test
    void shouldAllowStatusTransitionFromPendingToCancelled() {
        // Cita inicial en estado PENDING
        Appointment appt = Appointment.builder()
                .patientId(1L)
                .doctorId(2L)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .status(AppointmentStatus.PENDING)
                .whatsappNumber("300")
                .build();

        // Transición de estado a CANCELLED
        appt.setStatus(AppointmentStatus.CANCELLED);

        // Validación
        assertEquals(AppointmentStatus.CANCELLED, appt.getStatus());
    }

    @Test
    void shouldAllowStatusTransitionFromPendingToCompleted() {
        // Cita inicial en estado PENDING
        Appointment appt = Appointment.builder()
                .patientId(3L)
                .doctorId(4L)
                .appointmentDate(LocalDateTime.now().minusDays(1))
                .status(AppointmentStatus.PENDING)
                .whatsappNumber("301")
                .build();

        // Transición de estado a COMPLETED
        appt.setStatus(AppointmentStatus.COMPLETED);

        // Validación
        assertEquals(AppointmentStatus.COMPLETED, appt.getStatus());
    }

    @Test
    void appointmentStatusShouldHaveFourValues() {
        // Validación de los 4 valores del enum AppointmentStatus
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

        // Validación del campo whatsappNumber
        assertEquals("WEB-BOOKING", appt.getWhatsappNumber());
    }

    @Test
    void appointmentDateShouldBeStoredCorrectly() {
        // Fecha esperada
        LocalDateTime expectedDate = LocalDateTime.of(2026, 6, 15, 14, 30);

        // Construcción de cita con esa fecha
        Appointment appt = Appointment.builder()
                .patientId(7L)
                .doctorId(8L)
                .appointmentDate(expectedDate)
                .status(AppointmentStatus.PENDING)
                .whatsappNumber("302")
                .build();

        // Validación de almacenamiento correcto de fecha
        assertEquals(expectedDate, appt.getAppointmentDate());
    }
}
