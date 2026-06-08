package com.groupsoft.piedrazul.availability.domain.model.strategy;

import com.groupsoft.piedrazul.availability.domain.model.Availability;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Interfaz del PATRÓN STRATEGY.
// Define el contrato que deben cumplir todas las estrategias de cálculo de franjas horarias.
// Cada implementación puede tener un algoritmo distinto (ej. franjas fijas, dinámicas, personalizadas).
public interface SlotCalculationStrategy {
    
    /**
     * Calcula las franjas horarias disponibles para un médico en una fecha específica.
     *
     * @param schedule Configuración de disponibilidad del médico (día, rango de horas, intervalo).
     * @param targetDate Fecha en la que se quiere calcular las franjas.
     * @return Lista de horas disponibles en esa fecha.
     */
    List<LocalTime> calculateAvailableSlots(Availability schedule, LocalDate targetDate);
    
    /**
     * Determina si esta estrategia soporta la configuración de disponibilidad dada.
     * Permite seleccionar dinámicamente la estrategia correcta en AvailabilityService.
     *
     * @param schedule Configuración de disponibilidad.
     * @return true si la estrategia aplica, false en caso contrario.
     */
    boolean supports(Availability schedule);
}
