package com.groupsoft.piedrazul.availability.application.service;

// Importa los DTOs usados para la comunicación entre capas
import com.groupsoft.piedrazul.availability.application.dto.AvailabilityRequestDTO;
import com.groupsoft.piedrazul.availability.application.dto.AvailabilityResponseDTO;


// Importa las entidades del dominio
import com.groupsoft.piedrazul.availability.domain.model.Availability;
import com.groupsoft.piedrazul.availability.domain.model.Doctor;

// Importa la interfaz del patrón Strategy
import com.groupsoft.piedrazul.availability.domain.model.strategy.SlotCalculationStrategy;

// Importa los repositorios de persistencia
import com.groupsoft.piedrazul.availability.domain.repository.AvailabilityRepository;
import com.groupsoft.piedrazul.availability.domain.repository.DoctorRepository;

// Importa librerías de JUnit para pruebas unitarias
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Importa librerías de Mockito para simular dependencias
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Importa clases de Java para fechas y horas
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

// Importa utilidades de colecciones
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Importa métodos estáticos para simplificar aserciones y verificaciones
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

// Extiende la clase de prueba con soporte de Mockito
@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    // Se usan mocks para simular dependencias externas
    @Mock
    private AvailabilityRepository availabilityRepository; // Mock del repositorio de disponibilidades

    @Mock
    private DoctorRepository doctorRepository; // Mock del repositorio de médicos

    @Mock
    private SlotCalculationStrategy strategy; // Mock de la estrategia de cálculo de franjas horarias

    // Servicio bajo prueba (SUT: System Under Test)
    private AvailabilityService availabilityService;

    // Objetos de prueba
    // Entidad Doctor simulada
    private Doctor doctor;
    // Entidad Availability simulada
    private Availability availability; 
    // DTO de entrada para configurar disponibilidad
    private AvailabilityRequestDTO request; 

    @BeforeEach
    void setUp() {
        
         // Inicializa el servicio con los mocks
        availabilityService = new AvailabilityService(
                availabilityRepository,
                doctorRepository,
                List.of(strategy));
        
        // Datos de prueba: un doctor activo con especialidad
        doctor = Doctor.builder()
                .id(1L)
                .fullName("Juan Perez")
                .specialty("Cardiologia")
                .active(true)
                .build();
        
        // Disponibilidad configurada para el doctor en un día específico
        availability = Availability.builder()
                .id(100L)
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8,0))
                .endTime(LocalTime.of(12,0))
                .intervalMinutes(30)
                .active(true)
                .build();
        
        // DTO de entrada para configurar disponibilidad
        request = new AvailabilityRequestDTO();
        request.setDoctorId(1L);
        request.setDayOfWeek(DayOfWeek.MONDAY);
        request.setStartTime(LocalTime.of(8,0));
        request.setEndTime(LocalTime.of(12,0));
        request.setIntervalMinutes(30);
    }

    @Test
    void shouldConfigureAvailabilitySuccessfully() {

        // Simula que el doctor existe
        when(doctorRepository.findById(1L))
                .thenReturn(Optional.of(doctor));
        
        // Simula que la disponibilidad se guarda correctamente
        when(availabilityRepository.save(any(Availability.class)))
                .thenReturn(availability);

        // Ejecuta el método y obtiene respuesta
        AvailabilityResponseDTO response =
                availabilityService.configureAvailability(request);

         // Verifica que la respuesta no sea nula y tenga los datos correctos
        assertNotNull(response);
        assertEquals(1L, response.getDoctorId());
        assertEquals("Juan Perez", response.getDoctorName());

        // Verifica que se haya invocado el método save
        verify(availabilityRepository).save(any(Availability.class));
    }

    @Test
    void shouldThrowExceptionWhenDoctorDoesNotExist() {

        // Simula que el doctor no existe
        when(doctorRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Verifica que se lance excepción
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> availabilityService.configureAvailability(request)
        );

        
        assertEquals("Doctor no encontrado", exception.getMessage());

        // Verifica que no se haya intentado guardar disponibilidad
        verify(availabilityRepository, never()).save(any());
    }

    @Test
    void shouldReturnAvailableSlotsSuccessfully() {

        LocalDate targetDate = LocalDate.of(2026,5,25);

        // Simula que el doctor tiene disponibilidad activa ese día
        when(availabilityRepository
                .findByDoctorIdAndDayOfWeekAndActiveTrue(
                        1L,
                        DayOfWeek.MONDAY
                ))
                .thenReturn(Optional.of(availability));

        // Simula que la estrategia soporta la disponibilidad
        when(strategy.supports(availability))
                .thenReturn(true);

        // Simula que la estrategia devuelve dos franjas horarias
        when(strategy.calculateAvailableSlots(availability, targetDate))
                .thenReturn(List.of(
                        LocalTime.of(8,0),
                        LocalTime.of(8,30)
                ));

        // Ejecuta el método
        List<LocalTime> result =
                availabilityService.getAvailableSlots(1L, targetDate);

        // Verifica que se obtuvieron las franjas esperadas
        assertEquals(2, result.size());
        assertTrue(result.contains(LocalTime.of(8,0)));
    }

    @Test
    void shouldReturnEmptyListWhenDoctorDoesNotWorkThatDay() {

        LocalDate targetDate = LocalDate.of(2026,5,25);

        // Simula que no hay disponibilidad para ese día
        when(availabilityRepository
                .findByDoctorIdAndDayOfWeekAndActiveTrue(
                        1L,
                        DayOfWeek.MONDAY
                ))
                .thenReturn(Optional.empty());

        // Ejecuta el método
        List<LocalTime> result =
                availabilityService.getAvailableSlots(1L, targetDate);

        // Verifica que la lista está vacía
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void shouldThrowExceptionWhenNoStrategySupportsAvailability() {

        LocalDate targetDate = LocalDate.of(2026,5,25);

        // Simula que existe disponibilidad
        when(availabilityRepository
                .findByDoctorIdAndDayOfWeekAndActiveTrue(
                        1L,
                        DayOfWeek.MONDAY
                ))
                .thenReturn(Optional.of(availability));

         // Simula que ninguna estrategia soporta la disponibilidad
        when(strategy.supports(availability))
                .thenReturn(false);

        // Verifica que se lance excepción
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> availabilityService.getAvailableSlots(1L, targetDate)
        );

        assertEquals(
                "No hay estrategia de calculo para esta configuracion",
                exception.getMessage()
        );
    }

    @Test
    void shouldUseCorrectStrategyToCalculateSlots() {

        LocalDate targetDate = LocalDate.of(2026,5,25);

        // Simula que existe disponibilidad
        when(availabilityRepository
                .findByDoctorIdAndDayOfWeekAndActiveTrue(
                        anyLong(),
                        any()
                ))
                .thenReturn(Optional.of(availability));

        // Simula que la estrategia soporta la disponibilidad
        when(strategy.supports(any()))
                .thenReturn(true);

        // Simula que la estrategia devuelve una franja horaria
        when(strategy.calculateAvailableSlots(any(), any()))
                .thenReturn(List.of(LocalTime.of(9,0)));

        // Ejecuta el método
        availabilityService.getAvailableSlots(1L, targetDate);

        // Verifica que se invocó el cálculo de franjas exactamente una vez
        verify(strategy, times(1))
                .calculateAvailableSlots(availability, targetDate);
    }
}
