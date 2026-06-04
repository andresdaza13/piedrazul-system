package com.groupsoft.piedrazul.availability.application.service;

import com.groupsoft.piedrazul.availability.application.dto.AvailabilityRequestDTO;
import com.groupsoft.piedrazul.availability.application.dto.AvailabilityResponseDTO;
import com.groupsoft.piedrazul.availability.domain.model.Availability;
import com.groupsoft.piedrazul.availability.domain.model.Doctor;
import com.groupsoft.piedrazul.availability.domain.repository.AvailabilityRepository;
import com.groupsoft.piedrazul.availability.domain.repository.DoctorRepository;
import com.groupsoft.piedrazul.availability.domain.model.strategy.SlotCalculationStrategy;
import com.groupsoft.piedrazul.availability.infrastructure.adapter.BookingClientAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;
    private final SystemConfigService systemConfigService;
    private final BookingClientAdapter bookingClientAdapter;

    // PATRON STRATEGY
    // Spring inyecta automaticamente TODAS las clases que implementen SlotCalculationStrategy
    private final List<SlotCalculationStrategy> strategies;

    @Transactional
    public AvailabilityResponseDTO configureAvailability(AvailabilityRequestDTO request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        Availability availability = Availability.builder()
                .doctor(doctor)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .intervalMinutes(request.getIntervalMinutes())
                .active(true)
                .build();

        Availability saved = availabilityRepository.save(availability);

        return AvailabilityResponseDTO.builder()
                .id(saved.getId())
                .doctorId(saved.getDoctor().getId())
                .doctorName(saved.getDoctor().getFullName())
                .dayOfWeek(saved.getDayOfWeek())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .intervalMinutes(saved.getIntervalMinutes())
                .active(saved.isActive())
                .build();
    }

    @Transactional(readOnly = true)
    public List<LocalTime> getAvailableSlots(Long doctorId, LocalDate targetDate) {
        validateDateWithinBookingWindow(targetDate);

        List<LocalTime> calculatedSlots = availabilityRepository
                .findByDoctorIdAndDayOfWeekAndActiveTrue(doctorId, targetDate.getDayOfWeek())
                .map(availability -> {
                    SlotCalculationStrategy selectedStrategy = strategies.stream()
                            .filter(strategy -> strategy.supports(availability))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException(
                                "No hay estrategia de calculo para esta configuracion"));
                    return selectedStrategy.calculateAvailableSlots(availability, targetDate);
                })
                .orElse(Collections.emptyList());

        List<LocalTime> occupied = bookingClientAdapter.getOccupiedSlots(doctorId, targetDate);
        return calculatedSlots.stream()
                .filter(slot -> !occupied.contains(slot))
                .collect(Collectors.toList());
    }

    private void validateDateWithinBookingWindow(LocalDate targetDate) {
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusWeeks(systemConfigService.getBookingWindowWeeks());
        if (targetDate.isBefore(today) || targetDate.isAfter(maxDate)) {
            throw new IllegalArgumentException(
                    "La fecha debe estar dentro de la ventana de agendamiento configurada.");
        }
    }
}