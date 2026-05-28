package com.groupsoft.piedrazul.availability.domain.model.strategy;

import com.groupsoft.piedrazul.availability.domain.model.Availability;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class StandardSlotCalculationStrategy implements SlotCalculationStrategy {

    @Override
    public List<LocalTime> calculateAvailableSlots(Availability schedule, LocalDate targetDate) {
        // Lista que almacenará los horarios disponibles calculados
        List<LocalTime> slots = new ArrayList<>();

        // Valida que la fecha corresponda al día configurado y que la disponibilidad esté activa
        if (!targetDate.getDayOfWeek().equals(schedule.getDayOfWeek()) || !schedule.isActive()) {
            return slots; 
        }
        
        // Inicializa el primer horario disponible desde la hora de inicio
        LocalTime currentSlot = schedule.getStartTime();
        
        // Genera horarios mientras no se exceda la hora final
        while (currentSlot.plusMinutes(schedule.getIntervalMinutes()).isBefore(schedule.getEndTime()) 
                || currentSlot.plusMinutes(schedule.getIntervalMinutes()).equals(schedule.getEndTime())) {

            // Agrega el horario actual a la lista
            slots.add(currentSlot);
            currentSlot = currentSlot.plusMinutes(schedule.getIntervalMinutes());
        }

        // Retorna la lista de horarios disponibles
        return slots;
    }

    @Override
    public boolean supports(Availability schedule) {
        // Esta estrategia soporta cualquier configuración estándar
        return true; 
    }
}
