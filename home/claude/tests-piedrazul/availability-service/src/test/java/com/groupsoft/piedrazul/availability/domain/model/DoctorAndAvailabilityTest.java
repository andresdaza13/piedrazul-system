package com.groupsoft.piedrazul.availability.domain.model;

import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para las entidades Doctor y Availability
 * del bounded context de Disponibilidad.
 */
class DoctorAndAvailabilityTest {

    // ─────────────────────────────────────────────
    // Doctor
    // ─────────────────────────────────────────────

    @Test
    void doctorBuilderShouldCreateActiveDoctor() {
        Doctor doctor = Doctor.builder()
                .fullName("Dr. Martínez")
                .specialty("Fisioterapia")
                .active(true)
                .build();

        assertEquals("Dr. Martínez", doctor.getFullName());
        assertEquals("Fisioterapia", doctor.getSpecialty());
        assertTrue(doctor.isActive());
    }

    @Test
    void doctorShouldAllowDeactivation() {
        Doctor doctor = Doctor.builder()
                .fullName("Dr. García")
                .specialty("Medicina General")
                .active(true)
                .build();

        doctor.setActive(false);

        assertFalse(doctor.isActive());
    }

    @Test
    void doctorBuilderShouldWorkWithoutOptionalFields() {
        // specialty no es obligatorio
        Doctor doctor = Doctor.builder()
                .fullName("Dr. Sin Especialidad")
                .active(true)
                .build();

        assertNotNull(doctor);
        assertNull(doctor.getSpecialty());
        assertTrue(doctor.isActive());
    }

    // ─────────────────────────────────────────────
    // Availability
    // ─────────────────────────────────────────────

    @Test
    void availabilityBuilderShouldSetAllFields() {
        Doctor doctor = Doctor.builder()
                .fullName("Dr. López")
                .specialty("Terapia")
                .active(true)
                .build();

        Availability availability = Availability.builder()
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .intervalMinutes(30)
                .active(true)
                .build();

        assertEquals(DayOfWeek.MONDAY, availability.getDayOfWeek());
        assertEquals(LocalTime.of(8, 0), availability.getStartTime());
        assertEquals(LocalTime.of(12, 0), availability.getEndTime());
        assertEquals(30, availability.getIntervalMinutes());
        assertTrue(availability.isActive());
        assertEquals("Dr. López", availability.getDoctor().getFullName());
    }

    @Test
    void availabilityStartTimeShouldBeBeforeEndTime() {
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end   = LocalTime.of(17, 0);

        assertTrue(start.isBefore(end),
                "La hora de inicio debe ser anterior a la hora de fin");
    }

    @Test
    void availabilityShouldAllowToggleActive() {
        Availability av = Availability.builder()
                .doctor(Doctor.builder().fullName("X").active(true).build())
                .dayOfWeek(DayOfWeek.FRIDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(13, 0))
                .intervalMinutes(45)
                .active(true)
                .build();

        av.setActive(false);
        assertFalse(av.isActive());
    }

    @Test
    void availabilityShouldSupportDifferentIntervals() {
        // 20 min
        Availability av20 = Availability.builder()
                .doctor(Doctor.builder().fullName("A").active(true).build())
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .intervalMinutes(20)
                .active(true)
                .build();

        // 60 min
        Availability av60 = Availability.builder()
                .doctor(Doctor.builder().fullName("B").active(true).build())
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .intervalMinutes(60)
                .active(true)
                .build();

        assertEquals(20, av20.getIntervalMinutes());
        assertEquals(60, av60.getIntervalMinutes());
    }
}
