package com.groupsoft.piedrazul.availability.domain.model.strategy;

import com.groupsoft.piedrazul.availability.domain.model.Availability;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SlotCalculationStrategy {
    
    List<LocalTime> calculateAvailableSlots(Availability schedule, LocalDate targetDate);
    
    boolean supports(Availability schedule);
}