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

// @Service: indica que esta clase es un componente de la capa de servicio.
// @RequiredArgsConstructor: genera automáticamente el constructor con los atributos finales.
@Service
@RequiredArgsConstructor
public class AvailabilityService {

    // Repositorio para persistir configuraciones de disponibilidad.
    private final AvailabilityRepository availabilityRepository;

    // Repositorio para acceder a los médicos registrados.
    private final DoctorRepository doctorRepository;

    // PATRÓN STRATEGY:
    // Spring inyecta automáticamente todas las implementaciones de SlotCalculationStrategy.
    // Esto permite seleccionar dinámicamente el algoritmo correcto para calcular las franjas.
    private final List<SlotCalculationStrategy> strategies;

    /**
     * Configura la disponibilidad de un médico en un día específico.
     * Persiste la configuración en la base de datos y devuelve un DTO de respuesta.
     */
    @Transactional
    public AvailabilityResponseDTO configureAvailability(AvailabilityRequestDTO request) {
        // Validar que el médico exista.
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        // Construir objeto Availability usando el patrón Builder.
        Availability availability = Availability.builder()
                .doctor(doctor)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .intervalMinutes(request.getIntervalMinutes())
                .active(true)
                .build();

        // Guardar en la base de datos.
        Availability saved = availabilityRepository.save(availability);

        // Transformar a DTO de respuesta.
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

    /**
     * Obtiene las franjas horarias disponibles de un médico en una fecha específica.
     * Aplica el patrón Strategy para calcular las franjas según la configuración.
     */
    @Transactional(readOnly = true)
    public List<LocalTime> getAvailableSlots(Long doctorId, LocalDate targetDate) {

        // 1. Buscar si el médico tiene disponibilidad activa en ese día de la semana.
        return availabilityRepository.findByDoctorIdAndDayOfWeekAndActiveTrue(
                doctorId, targetDate.getDayOfWeek())
                .map(availability -> {
                    // 2. Seleccionar la estrategia correcta según la configuración.
                    SlotCalculationStrategy selectedStrategy = strategies.stream()
                            .filter(strategy -> strategy.supports(availability))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException(
                                "No hay estrategia de cálculo para esta configuración"));

                    // 3. Ejecutar el algoritmo de cálculo de franjas.
                    return selectedStrategy.calculateAvailableSlots(availability, targetDate);
                })
                // Si el médico no trabaja ese día, retornar lista vacía.
                .orElse(Collections.emptyList());
    }
}
