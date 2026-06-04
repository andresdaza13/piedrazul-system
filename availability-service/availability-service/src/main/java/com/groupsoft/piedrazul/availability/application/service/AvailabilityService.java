package com.groupsoft.piedrazul.availability.application.service;

import com.groupsoft.piedrazul.availability.application.dto.AvailabilityRequestDTO;
import com.groupsoft.piedrazul.availability.application.dto.AvailabilityResponseDTO;
import com.groupsoft.piedrazul.availability.domain.model.Availability;
import com.groupsoft.piedrazul.availability.domain.model.Doctor;
import com.groupsoft.piedrazul.availability.domain.repository.AvailabilityRepository;
import com.groupsoft.piedrazul.availability.domain.repository.DoctorRepository;
import com.groupsoft.piedrazul.availability.domain.model.strategy.SlotCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;

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

        // 1. Buscamos si el medico trabaja ese dia de la semana
        return availabilityRepository.findByDoctorIdAndDayOfWeekAndActiveTrue(
                doctorId, targetDate.getDayOfWeek())
                .map(availability -> {
                    // 2. Buscamos la estrategia correcta
                    SlotCalculationStrategy selectedStrategy = strategies.stream()
                            .filter(strategy -> strategy.supports(availability))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException(
                                "No hay estrategia de calculo para esta configuracion"));

                    // 3. Ejecutamos el algoritmo
                    return selectedStrategy.calculateAvailableSlots(availability, targetDate);
                })
                // Si no trabaja ese dia, retornamos lista vacia
                .orElse(Collections.emptyList());
    }
}
