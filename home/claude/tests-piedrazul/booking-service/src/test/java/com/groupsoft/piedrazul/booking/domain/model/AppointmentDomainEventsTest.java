package com.groupsoft.piedrazul.booking.domain.model;

import com.groupsoft.piedrazul.booking.domain.event.AppointmentCreatedEvent;
import com.groupsoft.piedrazul.booking.domain.exception.AppointmentOverlapException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para AppointmentCreatedEvent (Java Record)
 * y AppointmentOverlapException del dominio de Agendamiento.
 */
class AppointmentDomainEventsTest {

    // ─────────────────────────────────────────────
    // AppointmentCreatedEvent (Java Record)
    // ─────────────────────────────────────────────

    @Test
    void eventRecordShouldStoreAllFields() {
        LocalDateTime date = LocalDateTime.of(2026, 6, 10, 9, 0);

        AppointmentCreatedEvent event = new AppointmentCreatedEvent(
                100L, 1L, 10L, date, "+573001234567");

        assertEquals(100L, event.appointmentId());
        assertEquals(1L,   event.patientId());
        assertEquals(10L,  event.doctorId());
        assertEquals(date, event.appointmentDate());
        assertEquals("+573001234567", event.whatsappNumber());
    }

    @Test
    void eventRecordShouldBeImmutable() {
        AppointmentCreatedEvent event = new AppointmentCreatedEvent(
                1L, 2L, 3L, LocalDateTime.now(), "300");

        // Los Records de Java son inmutables — no tienen setters
        // Este test verifica que los datos sean los mismos después de creados
        assertEquals(1L, event.appointmentId());
        assertEquals(2L, event.patientId());
        assertEquals(3L, event.doctorId());
    }

    @Test
    void twoEventsWithSameDataShouldBeEqual() {
        LocalDateTime date = LocalDateTime.of(2026, 6, 10, 9, 0);

        AppointmentCreatedEvent event1 = new AppointmentCreatedEvent(
                1L, 2L, 3L, date, "300");
        AppointmentCreatedEvent event2 = new AppointmentCreatedEvent(
                1L, 2L, 3L, date, "300");

        // Los Records implementan equals() automáticamente
        assertEquals(event1, event2);
    }

    @Test
    void twoEventsWithDifferentDataShouldNotBeEqual() {
        LocalDateTime date = LocalDateTime.of(2026, 6, 10, 9, 0);

        AppointmentCreatedEvent event1 = new AppointmentCreatedEvent(
                1L, 2L, 3L, date, "300");
        AppointmentCreatedEvent event2 = new AppointmentCreatedEvent(
                99L, 2L, 3L, date, "300");

        assertNotEquals(event1, event2);
    }

    // ─────────────────────────────────────────────
    // AppointmentOverlapException
    // ─────────────────────────────────────────────

    @Test
    void overlapExceptionShouldPreserveMessage() {
        String msg = "El doctor ya tiene una cita reservada en esa fecha y hora.";

        AppointmentOverlapException ex = new AppointmentOverlapException(msg);

        assertEquals(msg, ex.getMessage());
    }

    @Test
    void overlapExceptionShouldExtendRuntimeException() {
        AppointmentOverlapException ex =
                new AppointmentOverlapException("Solapamiento");

        assertInstanceOf(RuntimeException.class, ex,
                "AppointmentOverlapException debe extender RuntimeException");
    }

    @Test
    void overlapExceptionShouldBeCaughtAsRuntimeException() {
        assertThrows(RuntimeException.class, () -> {
            throw new AppointmentOverlapException("Conflicto de horario");
        });
    }
}
