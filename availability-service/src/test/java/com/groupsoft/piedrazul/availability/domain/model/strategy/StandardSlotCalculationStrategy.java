package com.groupsoft.piedrazul.availability.domain.model.strategy;

import com.groupsoft.piedrazul.availability.domain.model.Availability;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StandardSlotCalculationStrategyTest {

    private StandardSlotCalculationStrategy strategy;
    private Availability availability;

    @BeforeEach
    void setUp() {

        strategy = new StandardSlotCalculationStrategy();

        availability = Availability.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8,0))
                .endTime(LocalTime.of(10,0))
                .intervalMinutes(30)
                .active(true)
                .build();
    }

    // TEST: calcula correctamente los horarios disponibles
    @Test
    void shouldCalculateSlotsCorrectly() {

        LocalDate date = LocalDate.of(2026,5,25);

        List<LocalTime> result =
                strategy.calculateAvailableSlots(
                        availability,
                        date
                );

        assertEquals(4, result.size());

        assertEquals(LocalTime.of(8,0), result.get(0));
        assertEquals(LocalTime.of(9,30), result.get(3));
    }

    // TEST: devuelve lista vacía si la disponibilidad está inactiva
    @Test
    void shouldReturnEmptyListWhenAvailabilityInactive() {

        availability.setActive(false);

        LocalDate date = LocalDate.of(2026,5,25);

        List<LocalTime> result =
                strategy.calculateAvailableSlots(
                        availability,
                        date
                );

        assertTrue(result.isEmpty());
    }

    // TEST: devuelve lista vacía si el día no coincide
    @Test
    void shouldReturnEmptyListWhenDayDoesNotMatch() {

        LocalDate date = LocalDate.of(2026,5,26);

        List<LocalTime> result =
                strategy.calculateAvailableSlots(
                        availability,
                        date
                );

        assertTrue(result.isEmpty());
    }

    // TEST: siempre soporta cualquier disponibilidad
    @Test
    void shouldAlwaysSupportAvailability() {

        assertTrue(strategy.supports(availability));
    }
}