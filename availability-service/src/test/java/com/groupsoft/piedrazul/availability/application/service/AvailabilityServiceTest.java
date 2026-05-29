package com.groupsoft.piedrazul.availability.application.service;

import com.groupsoft.piedrazul.availability.application.dto.AvailabilityRequestDTO;
import com.groupsoft.piedrazul.availability.application.dto.AvailabilityResponseDTO;
import com.groupsoft.piedrazul.availability.domain.model.Availability;
import com.groupsoft.piedrazul.availability.domain.model.Doctor;
import com.groupsoft.piedrazul.availability.domain.model.strategy.SlotCalculationStrategy;
import com.groupsoft.piedrazul.availability.domain.repository.AvailabilityRepository;
import com.groupsoft.piedrazul.availability.domain.repository.DoctorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SlotCalculationStrategy strategy;

    private AvailabilityService availabilityService;

    private Doctor doctor;
    private Availability availability;
    private AvailabilityRequestDTO request;

    @BeforeEach
    void setUp() {

        availabilityService = new AvailabilityService(
                availabilityRepository,
                doctorRepository,
                List.of(strategy));

        doctor = Doctor.builder()
                .id(1L)
                .fullName("Juan Perez")
                .specialty("Cardiologia")
                .active(true)
                .build();

        availability = Availability.builder()
                .id(100L)
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8,0))
                .endTime(LocalTime.of(12,0))
                .intervalMinutes(30)
                .active(true)
                .build();

        request = new AvailabilityRequestDTO();
        request.setDoctorId(1L);
        request.setDayOfWeek(DayOfWeek.MONDAY);
        request.setStartTime(LocalTime.of(8,0));
        request.setEndTime(LocalTime.of(12,0));
        request.setIntervalMinutes(30);
    }

    // TEST: configura la disponibilidad correctamente
    @Test
    void shouldConfigureAvailabilitySuccessfully() {

        when(doctorRepository.findById(1L))
                .thenReturn(Optional.of(doctor));

        when(availabilityRepository.save(any(Availability.class)))
                .thenReturn(availability);

        AvailabilityResponseDTO response =
                availabilityService.configureAvailability(request);

        assertNotNull(response);
        assertEquals(1L, response.getDoctorId());
        assertEquals("Juan Perez", response.getDoctorName());

        verify(availabilityRepository).save(any(Availability.class));
    }

    // TEST: lanza una excepción si el doctor no existe
    @Test
    void shouldThrowExceptionWhenDoctorDoesNotExist() {

        when(doctorRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> availabilityService.configureAvailability(request)
        );

        assertEquals("Doctor no encontrado", exception.getMessage());

        verify(availabilityRepository, never()).save(any());
    }

    // TEST: devuelve los horarios disponibles correctamente
    @Test
    void shouldReturnAvailableSlotsSuccessfully() {

        LocalDate targetDate = LocalDate.of(2026,5,25);

        when(availabilityRepository
                .findByDoctorIdAndDayOfWeekAndActiveTrue(
                        1L,
                        DayOfWeek.MONDAY
                ))
                .thenReturn(Optional.of(availability));

        when(strategy.supports(availability))
                .thenReturn(true);

        when(strategy.calculateAvailableSlots(availability, targetDate))
                .thenReturn(List.of(
                        LocalTime.of(8,0),
                        LocalTime.of(8,30)
                ));

        List<LocalTime> result =
                availabilityService.getAvailableSlots(1L, targetDate);

        assertEquals(2, result.size());
        assertTrue(result.contains(LocalTime.of(8,0)));
    }

    // TEST: lanza excepción cuando existe solapamiento de citas
    @Test
    void shouldReturnEmptyListWhenDoctorDoesNotWorkThatDay() {

        LocalDate targetDate = LocalDate.of(2026,5,25);

        when(availabilityRepository
                .findByDoctorIdAndDayOfWeekAndActiveTrue(
                        1L,
                        DayOfWeek.MONDAY
                ))
                .thenReturn(Optional.empty());

        List<LocalTime> result =
                availabilityService.getAvailableSlots(1L, targetDate);

        assertEquals(Collections.emptyList(), result);
    }

    // TEST: lanza una excepción cuando no hay estrategia complatible
    @Test
    void shouldThrowExceptionWhenNoStrategySupportsAvailability() {

        LocalDate targetDate = LocalDate.of(2026,5,25);

        when(availabilityRepository
                .findByDoctorIdAndDayOfWeekAndActiveTrue(
                        1L,
                        DayOfWeek.MONDAY
                ))
                .thenReturn(Optional.of(availability));

        when(strategy.supports(availability))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> availabilityService.getAvailableSlots(1L, targetDate)
        );

        assertEquals(
                "No hay estrategia de calculo para esta configuracion",
                exception.getMessage()
        );
    }

    // TEST: usa la estrategia correcta para calcular horarios
    @Test
    void shouldUseCorrectStrategyToCalculateSlots() {

        LocalDate targetDate = LocalDate.of(2026,5,25);

        when(availabilityRepository
                .findByDoctorIdAndDayOfWeekAndActiveTrue(
                        anyLong(),
                        any()
                ))
                .thenReturn(Optional.of(availability));

        when(strategy.supports(any()))
                .thenReturn(true);

        when(strategy.calculateAvailableSlots(any(), any()))
                .thenReturn(List.of(LocalTime.of(9,0)));

        availabilityService.getAvailableSlots(1L, targetDate);

        verify(strategy, times(1))
                .calculateAvailableSlots(availability, targetDate);
    }
}