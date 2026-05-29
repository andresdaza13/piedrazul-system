package com.groupsoft.piedrazul.availability.domain.model.strategy;

import com.groupsoft.piedrazul.availability.domain.model.Availability;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

/**
 * PATRON TEMPLATE METHOD (Comportamiento): define el esqueleto del calculo de franjas.
 * Las subclases implementan los pasos variables sin duplicar la estructura del algoritmo.
 */
public abstract class AbstractSlotCalculationStrategy implements SlotCalculationStrategy {

    @Override
    public final List<LocalTime> calculateAvailableSlots(Availability schedule, LocalDate targetDate) {
        if (!isScheduleApplicable(schedule, targetDate)) {
            return Collections.emptyList();
        }
        return generateTimeSlots(schedule, targetDate);
    }

    protected boolean isScheduleApplicable(Availability schedule, LocalDate targetDate) {
        return targetDate.getDayOfWeek().equals(schedule.getDayOfWeek()) && schedule.isActive();
    }

    protected abstract List<LocalTime> generateTimeSlots(Availability schedule, LocalDate targetDate);
}
