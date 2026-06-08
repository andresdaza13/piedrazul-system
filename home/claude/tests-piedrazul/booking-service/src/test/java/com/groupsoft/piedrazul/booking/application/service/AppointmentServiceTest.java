package com.groupsoft.piedrazul.booking.application.service;
// Paquete de pruebas unitarias para la capa de aplicación del bounded context Booking

// ─────────────────────────────────────────────
// Imports de clases propias del dominio y aplicación
// ─────────────────────────────────────────────
import com.groupsoft.piedrazul.booking.application.dto.AppointmentRequestDTO; 
// DTO de entrada para crear una cita (paciente, doctor, fecha, hora, etc.)

import com.groupsoft.piedrazul.booking.application.dto.AppointmentResponseDTO; 
// DTO de salida que representa la respuesta al crear o consultar citas

import com.groupsoft.piedrazul.booking.domain.Repository.AppointmentRepository; 
// Repositorio JPA para acceder y persistir citas en la base de datos

import com.groupsoft.piedrazul.booking.domain.exception.AppointmentOverlapException; 
// Excepción personalizada lanzada cuando hay solapamiento de citas

import com.groupsoft.piedrazul.booking.domain.model.Appointment; 
// Entidad del dominio que representa una cita médica

import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus; 
// Enum que define los estados de una cita (PENDING, CONFIRMED, etc.)

// ─────────────────────────────────────────────
// Imports de JUnit 5 para pruebas unitarias
// ─────────────────────────────────────────────
import org.junit.jupiter.api.BeforeEach; 
// Anotación para ejecutar un método antes de cada prueba

import org.junit.jupiter.api.Test; 
// Anotación para marcar un método como caso de prueba

import org.junit.jupiter.api.extension.ExtendWith; 
// Permite extender el comportamiento de JUnit con extensiones (ej. Mockito)

// ─────────────────────────────────────────────
// Imports de Mockito para simulación de dependencias
// ─────────────────────────────────────────────
import org.mockito.InjectMocks; 
// Inyecta los mocks en la clase bajo prueba (AppointmentService)

import org.mockito.Mock; 
// Marca un objeto como mock (dependencia simulada, ej. repositorio o publisher)

import org.mockito.junit.jupiter.MockitoExtension; 
// Extensión de Mockito para integrarse con JUnit 5

// ─────────────────────────────────────────────
// Imports de Java estándar para fechas y colecciones
// ─────────────────────────────────────────────
import java.time.LocalDate; 
// Representa una fecha sin zona horaria

import java.time.LocalDateTime; 
// Representa fecha y hora combinadas

import java.util.List; 
// Colección de tipo lista para manejar resultados de citas

// ─────────────────────────────────────────────
// Imports estáticos para simplificar llamadas en pruebas
// ─────────────────────────────────────────────
import static org.junit.jupiter.api.Assertions.*; 
// Métodos de aserción de JUnit (assertEquals, assertTrue, assertThrows, etc.)

import static org.mockito.ArgumentMatchers.any; 
// Matcher de Mockito para aceptar cualquier argumento en un mock

import static org.mockito.ArgumentMatchers.eq; 
// Matcher de Mockito para verificar igualdad exacta de argumentos

import static org.mockito.Mockito.*; 
// Métodos de verificación y configuración de Mockito (when, verify, times, never, etc.)

/**
 * Pruebas unitarias para AppointmentService.
 * Cubre RF1 (listar citas) y RF2 (crear cita desde WhatsApp),
 * incluyendo la validación de solapamiento y la publicación de eventos.
 */
@ExtendWith(MockitoExtension.class) // Extiende JUnit con soporte de Mockito
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository; // Mock del repositorio de citas

    @Mock
    private AppointmentEventPublisher eventPublisher; // Mock del publicador de eventos

    @InjectMocks
    private AppointmentService appointmentService; // Clase bajo prueba (SUT)

    private AppointmentRequestDTO requestDTO; // DTO de entrada para pruebas
    private Appointment savedAppointment;     // Cita simulada guardada en BD

    @BeforeEach
    void setUp() {
        // Configuración inicial antes de cada prueba
        requestDTO = new AppointmentRequestDTO();
        requestDTO.setPatientId(1L);
        requestDTO.setDoctorId(10L);
        requestDTO.setAppointmentDate(LocalDateTime.of(2026, 6, 10, 9, 0));
        requestDTO.setWhatsappNumber("+573001234567");
        requestDTO.setNotes("Consulta general");

        // Cita simulada que representa un registro exitoso
        savedAppointment = Appointment.builder()
                .id(100L)
                .patientId(1L)
                .doctorId(10L)
                .appointmentDate(LocalDateTime.of(2026, 6, 10, 9, 0))
                .status(AppointmentStatus.PENDING)
                .whatsappNumber("+573001234567")
                .notes("Consulta general")
                .build();
    }

    // ─────────────────────────────────────────────
    // RF2 – Crear cita desde WhatsApp
    // ─────────────────────────────────────────────

    @Test
    void shouldCreateAppointmentSuccessfullyWhenSlotIsAvailable() {
        // Arrange - el slot está libre
        when(repository.existsByDoctorIdAndAppointmentDate(10L,
                LocalDateTime.of(2026, 6, 10, 9, 0)))
                .thenReturn(false);
        when(repository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // Act - se crea la cita
        AppointmentResponseDTO response = appointmentService.createAppointment(requestDTO);

        // Assert - validaciones
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(AppointmentStatus.PENDING, response.getStatus());
        verify(repository, times(1)).save(any(Appointment.class));
    }

    @Test
    void shouldThrowAppointmentOverlapExceptionWhenSlotIsOccupied() {
        // Arrange - el slot ya está ocupado
        when(repository.existsByDoctorIdAndAppointmentDate(any(), any()))
                .thenReturn(true);

        // Act & Assert - debe lanzar excepción
        AppointmentOverlapException ex = assertThrows(
                AppointmentOverlapException.class,
                () -> appointmentService.createAppointment(requestDTO));

        assertTrue(ex.getMessage().contains("ya tiene una cita"),
                "El mensaje debe indicar solapamiento");
        verify(repository, never()).save(any(Appointment.class));
    }

    @Test
    void newAppointmentShouldAlwaysStartWithStatusPending() {
        // Arrange - cita nueva
        when(repository.existsByDoctorIdAndAppointmentDate(any(), any()))
                .thenReturn(false);
        when(repository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // Act
        AppointmentResponseDTO response = appointmentService.createAppointment(requestDTO);

        // Assert - toda cita nueva inicia en estado PENDING
        assertEquals(AppointmentStatus.PENDING, response.getStatus(),
                "Toda cita nueva debe iniciar en estado PENDING");
    }

    @Test
    void shouldPublishAppointmentCreatedEventAfterSave() {
        // Arrange - cita guardada correctamente
        when(repository.existsByDoctorIdAndAppointmentDate(any(), any()))
                .thenReturn(false);
        when(repository.save(any(Appointment.class))).thenReturn(savedAppointment);
        doNothing().when(eventPublisher).publishAppointmentCreatedEvent(any());

        // Act
        appointmentService.createAppointment(requestDTO);

        // Assert - evento publicado tras guardar
        verify(eventPublisher, times(1)).publishAppointmentCreatedEvent(any());
    }

    @Test
    void shouldNotPublishEventIfSaveFails() {
        // Arrange - falla al guardar
        when(repository.existsByDoctorIdAndAppointmentDate(any(), any()))
                .thenReturn(false);
        when(repository.save(any(Appointment.class)))
                .thenThrow(new RuntimeException("Error de BD"));

        // Act & Assert - debe lanzar excepción y no publicar evento
        assertThrows(RuntimeException.class,
                () -> appointmentService.createAppointment(requestDTO));

        verify(eventPublisher, never()).publishAppointmentCreatedEvent(any());
    }

    // ─────────────────────────────────────────────
    // RF1 – Listar citas de un médico en una fecha
    // ─────────────────────────────────────────────

    @Test
    void shouldReturnAppointmentsForDoctorOnGivenDate() {
        // Arrange - citas existentes para el médico
        LocalDate date = LocalDate.of(2026, 6, 10);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.atTime(23, 59, 59);

        when(repository.findByDoctorIdAndAppointmentDateBetween(10L, start, end))
                .thenReturn(List.of(savedAppointment));

        // Act
        List<AppointmentResponseDTO> results =
                appointmentService.getAppointmentsByDoctorAndDate(10L, date);

        // Assert
        assertEquals(1, results.size());
        assertEquals(10L, results.get(0).getDoctorId());
    }

    @Test
    void shouldReturnEmptyListWhenNoCitasForDoctor() {
        // Arrange - sin citas para el médico
        LocalDate date = LocalDate.of(2026, 6, 10);
        when(repository.findByDoctorIdAndAppointmentDateBetween(any(), any(), any()))
                .thenReturn(List.of());

        // Act
        List<AppointmentResponseDTO> results =
                appointmentService.getAppointmentsByDoctorAndDate(99L, date);

        // Assert - lista vacía
        assertTrue(results.isEmpty(),
                "Debe retornar lista vacía si no hay citas para ese médico y fecha");
    }

    @Test
    void shouldQueryCorrectDateRangeForGivenDate() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 6, 15);
        LocalDateTime expectedStart = LocalDateTime.of(2026, 6, 15, 0, 0, 0);
        LocalDateTime expectedEnd   = LocalDateTime.of(2026, 6, 15, 23, 59, 59);

        when(repository.findByDoctorIdAndAppointmentDateBetween(
                eq(10L), eq(expectedStart), eq(expectedEnd)))
                .thenReturn(List.of());

        // Act
        appointmentService.getAppointmentsByDoctorAndDate(10L, date);

        // Assert - verificamos que el rango de fechas sea exactamente el del día
        verify(repository).findByDoctorIdAndAppointmentDateBetween(
                eq(10L), eq(expectedStart), eq(expectedEnd));
    }

    
}
