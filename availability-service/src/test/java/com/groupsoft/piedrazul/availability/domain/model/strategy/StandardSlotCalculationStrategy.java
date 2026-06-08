package com.groupsoft.piedrazul.availability.domain.model.strategy; // Paquete donde se ubica la clase de prueba

// Importa la entidad Availability usada en las pruebas
import com.groupsoft.piedrazul.availability.domain.model.Availability;

// Importa librerías de JUnit para pruebas unitarias
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Importa clases de Java para fechas y horas
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

// Importa utilidades de colecciones
import java.util.List;

// Importa métodos estáticos para simplificar aserciones
import static org.junit.jupiter.api.Assertions.*;

// Clase de prueba para la estrategia estándar de cálculo de franjas horarias
class StandardSlotCalculationStrategyTest {

    private StandardSlotCalculationStrategy strategy; // Estrategia bajo prueba (SUT)
    private Availability availability; // Entidad Availability simulada

    @BeforeEach
    void setUp() {
        // Inicializa la estrategia estándar
        strategy = new StandardSlotCalculationStrategy();

        // Configura una disponibilidad de prueba: lunes de 8:00 a 10:00 con intervalos de 30 minutos
        availability = Availability.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8,0))
                .endTime(LocalTime.of(10,0))
                .intervalMinutes(30)
                .active(true)
                .build();
    }

    @Test
    void shouldCalculateSlotsCorrectly() {
        // Fecha que corresponde a un lunes
        LocalDate date = LocalDate.of(2026,5,25);

        // Ejecuta el cálculo de franjas
        List<LocalTime> result =
                strategy.calculateAvailableSlots(
                        availability,
                        date
                );

        // Verifica que se generaron 4 franjas: 8:00, 8:30, 9:00, 9:30
        assertEquals(4, result.size());

        // Verifica la primera franja
        assertEquals(LocalTime.of(8,0), result.get(0));
        // Verifica la última franja
        assertEquals(LocalTime.of(9,30), result.get(3));
    }

    @Test
    void shouldReturnEmptyListWhenAvailabilityInactive() {
        // Marca la disponibilidad como inactiva
        availability.setActive(false);

        LocalDate date = LocalDate.of(2026,5,25);

        // Ejecuta el cálculo de franjas
        List<LocalTime> result =
                strategy.calculateAvailableSlots(
                        availability,
                        date
                );

        // Verifica que no se generen franjas
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenDayDoesNotMatch() {
        // Fecha que corresponde a un martes (no coincide con la disponibilidad configurada en lunes)
        LocalDate date = LocalDate.of(2026,5,26);

        // Ejecuta el cálculo de franjas
        List<LocalTime> result =
                strategy.calculateAvailableSlots(
                        availability,
                        date
                );

        // Verifica que no se generen franjas
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldAlwaysSupportAvailability() {
        // Verifica que la estrategia estándar siempre soporte cualquier disponibilidad
        assertTrue(strategy.supports(availability));
    }
}
