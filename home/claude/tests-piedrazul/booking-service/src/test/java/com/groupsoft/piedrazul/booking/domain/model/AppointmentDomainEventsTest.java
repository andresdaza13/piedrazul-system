package com.groupsoft.piedrazul.booking.domain.model;
// Paquete de pruebas unitarias para las entidades y eventos del dominio Booking

import com.groupsoft.piedrazul.booking.domain.event.AppointmentCreatedEvent; 
// Evento de dominio (Java Record) que representa la creación de una cita

import com.groupsoft.piedrazul.booking.domain.exception.AppointmentOverlapException; 
// Excepción personalizada lanzada cuando hay solapamiento de citas

import org.junit.jupiter.api.Test; 
// Anotación de JUnit 5 para definir métodos de prueba

import java.time.LocalDateTime; 
// Clase estándar de Java para manejar fecha y hora

import static org.junit.jupiter.api.Assertions.*; 
// Métodos de aserción de JUnit (assertEquals, assertThrows, assertNotEquals, etc.)

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
        // Arrange - fecha de la cita
        LocalDateTime date = LocalDateTime.of(2026, 6, 10, 9, 0);

        // Act - creación del evento con todos los campos
        AppointmentCreatedEvent event = new AppointmentCreatedEvent(
                100L, 1L, 10L, date, "+573001234567");

        // Assert - validación de cada campo
        assertEquals(100L, event.appointmentId());
        assertEquals(1L,   event.patientId());
        assertEquals(10L,  event.doctorId());
        assertEquals(date, event.appointmentDate());
        assertEquals("+573001234567", event.whatsappNumber());
    }

    @Test
    void eventRecordShouldBeImmutable() {
        // Arrange - creación de evento
        AppointmentCreatedEvent event = new AppointmentCreatedEvent(
                1L, 2L, 3L, LocalDateTime.now(), "300");

        // Assert - los Records son inmutables, no tienen setters
        assertEquals(1L, event.appointmentId());
        assertEquals(2L, event.patientId());
        assertEquals(3L, event.doctorId());
    }

    @Test
    void twoEventsWithSameDataShouldBeEqual() {
        // Arrange - dos eventos con mismos datos
        LocalDateTime date = LocalDateTime.of(2026, 6, 10, 9, 0);

        AppointmentCreatedEvent event1 = new AppointmentCreatedEvent(
                1L, 2L, 3L, date, "300");
        AppointmentCreatedEvent event2 = new AppointmentCreatedEvent(
                1L, 2L, 3L, date, "300");

        // Assert - los Records implementan equals() automáticamente
        assertEquals(event1, event2);
    }

    @Test
    void twoEventsWithDifferentDataShouldNotBeEqual() {
        // Arrange - dos eventos con datos distintos
        LocalDateTime date = LocalDateTime.of(2026, 6, 10, 9, 0);

        AppointmentCreatedEvent event1 = new AppointmentCreatedEvent(
                1L, 2L, 3L, date, "300");
        AppointmentCreatedEvent event2 = new AppointmentCreatedEvent(
                99L, 2L, 3L, date, "300");

        // Assert - deben ser diferentes
        assertNotEquals(event1, event2);
    }

    // ─────────────────────────────────────────────
    // AppointmentOverlapException
    // ─────────────────────────────────────────────

    @Test
    void overlapExceptionShouldPreserveMessage() {
        // Arrange - mensaje de error
        String msg = "El doctor ya tiene una cita reservada en esa fecha y hora.";

        // Act - creación de excepción
        AppointmentOverlapException ex = new AppointmentOverlapException(msg);

        // Assert - el mensaje debe preservarse
        assertEquals(msg, ex.getMessage());
    }

    @Test
    void overlapExceptionShouldExtendRuntimeException() {
        // Act - creación de excepción
        AppointmentOverlapException ex =
                new AppointmentOverlapException("Solapamiento");

        // Assert - debe extender RuntimeException
        assertInstanceOf(RuntimeException.class, ex,
                "AppointmentOverlapException debe extender RuntimeException");
    }

    @Test
    void overlapExceptionShouldBeCaughtAsRuntimeException() {
        // Assert - debe poder capturarse como RuntimeException
        assertThrows(RuntimeException.class, () -> {
            throw new AppointmentOverlapException("Conflicto de horario");
        });
    }
}
