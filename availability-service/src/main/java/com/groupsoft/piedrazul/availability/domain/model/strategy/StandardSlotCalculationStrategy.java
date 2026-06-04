package com.groupsoft.piedrazul.availability.domain.model.strategy;

import com.groupsoft.piedrazul.availability.domain.model.Availability;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class StandardSlotCalculationStrategy extends AbstractSlotCalculationStrategy {

    @Override
    protected List<LocalTime> generateTimeSlots(Availability schedule, LocalDate targetDate) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime currentSlot = schedule.getStartTime();

        while (currentSlot.plusMinutes(schedule.getIntervalMinutes()).isBefore(schedule.getEndTime())
                || currentSlot.plusMinutes(schedule.getIntervalMinutes()).equals(schedule.getEndTime())) {
            slots.add(currentSlot);
            currentSlot = currentSlot.plusMinutes(schedule.getIntervalMinutes());
        }
        return slots;
    }

    @Override
    public boolean supports(Availability schedule) {
        return true;
    }
}
