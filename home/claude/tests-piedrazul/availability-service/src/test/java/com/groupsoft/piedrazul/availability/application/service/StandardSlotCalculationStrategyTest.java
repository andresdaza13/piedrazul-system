package com.groupsoft.piedrazul.availability.application.service; 
// Paquete de pruebas dentro de la capa de aplicación del bounded context Disponibilidad

// Entidad Availability
import com.groupsoft.piedrazul.availability.domain.model.Availability; 

// Entidad Doctor
import com.groupsoft.piedrazul.availability.domain.model.Doctor;

// Estrategia a probar
import com.groupsoft.piedrazul.availability.domain.model.strategy.StandardSlotCalculationStrategy; 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para StandardSlotCalculationStrategy.
 * Cubre el patrón STRATEGY del bounded context de Disponibilidad.
 */
class StandardSlotCalculationStrategyTest {

    private StandardSlotCalculationStrategy strategy; // Estrategia bajo prueba (SUT)
    private Doctor doctor; // Entidad Doctor usada en las pruebas

    @BeforeEach
    void setUp() {
        // Inicializa la estrategia estándar
        strategy = new StandardSlotCalculationStrategy();

        // Crea un doctor de prueba
        doctor = Doctor.builder()
                .fullName("Dr. Prueba")
                .specialty("General")
                .active(true)
                .build();
    }

    // Método auxiliar para construir Availability con parámetros dinámicos
    private Availability buildAvailability(DayOfWeek day, LocalTime start,
                                           LocalTime end, int interval, boolean active) {
        return Availability.builder()
                .doctor(doctor)
                .dayOfWeek(day)
                .startTime(start)
                .endTime(end)
                .intervalMinutes(interval)
                .active(active)
                .build();
    }

    // ─────────────────────────────────────────────
    // Generación correcta de slots
    // ─────────────────────────────────────────────

    @Test
    void shouldGenerate8SlotsFor4HoursAt30MinInterval() {
        // 08:00 → 12:00 cada 30 min = 8 slots
        Availability av = buildAvailability(
                DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0), 30, true);

        // Fecha: próximo lunes
        LocalDate nextMonday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        List<LocalTime> slots = strategy.calculateAvailableSlots(av, nextMonday);

        assertEquals(8, slots.size(), "Deben generarse 8 slots de 30 min en 4 horas");
        assertEquals(LocalTime.of(8, 0),  slots.get(0));
        assertEquals(LocalTime.of(11, 30), slots.get(7));
    }

    @Test
    void shouldGenerate4SlotsFor4HoursAt60MinInterval() {
        // 08:00 → 12:00 cada 60 min = 4 slots
        Availability av = buildAvailability(
                DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0), 60, true);

        LocalDate nextTuesday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
        List<LocalTime> slots = strategy.calculateAvailableSlots(av, nextTuesday);

        assertEquals(4, slots.size());
        assertEquals(LocalTime.of(8, 0),  slots.get(0));
        assertEquals(LocalTime.of(11, 0), slots.get(3));
    }

    @Test
    void firstSlotShouldAlwaysBeStartTime() {
        Availability av = buildAvailability(
                DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0), 30, true);

        LocalDate nextWed = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));
        List<LocalTime> slots = strategy.calculateAvailableSlots(av, nextWed);

        assertFalse(slots.isEmpty());
        assertEquals(LocalTime.of(9, 0), slots.get(0),
                "El primer slot siempre debe coincidir con startTime");
    }

    @Test
    void slotsShouldBeConsecutiveAndEquallySpaced() {
        Availability av = buildAvailability(
                DayOfWeek.THURSDAY, LocalTime.of(8, 0), LocalTime.of(10, 0), 30, true);

        LocalDate nextThu = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
        List<LocalTime> slots = strategy.calculateAvailableSlots(av, nextThu);

        for (int i = 1; i < slots.size(); i++) {
            assertEquals(30, java.time.Duration.between(slots.get(i - 1), slots.get(i)).toMinutes(),
                    "Cada slot debe estar separado exactamente 30 minutos del anterior");
        }
    }

    // ─────────────────────────────────────────────
    // Casos que deben retornar lista vacía
    // ─────────────────────────────────────────────

    @Test
    void shouldReturnEmptySlotsWhenDayDoesNotMatch() {
        // Disponibilidad configurada para lunes, pero consultamos martes
        Availability av = buildAvailability(
                DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0), 30, true);

        LocalDate nextTuesday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
        List<LocalTime> slots = strategy.calculateAvailableSlots(av, nextTuesday);

        assertTrue(slots.isEmpty(),
                "No deben generarse slots para un día que no coincide con el configurado");
    }

    @Test
    void shouldReturnEmptySlotsWhenAvailabilityIsInactive() {
        Availability av = buildAvailability(
                DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(12, 0), 30, false);

        LocalDate nextFriday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        List<LocalTime> slots = strategy.calculateAvailableSlots(av, nextFriday);

        assertTrue(slots.isEmpty(),
                "No deben generarse slots si la disponibilidad está inactiva");
    }

    // ─────────────────────────────────────────────
    // supports()
    // ─────────────────────────────────────────────

    @Test
    void supportsShouldAlwaysReturnTrue() {
        Availability av = buildAvailability(
                DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0), 30, true);

        assertTrue(strategy.supports(av),
                "StandardSlotCalculationStrategy soporta cualquier configuración");
    }
}
