package com.groupsoft.piedrazul.user.application.service;
// Paquete de pruebas unitarias para la capa de aplicación del bounded context User

import com.groupsoft.piedrazul.user.application.dto.UserWhatsAppDTO; 
// DTO que representa los datos de usuario recibidos desde WhatsApp

import com.groupsoft.piedrazul.user.domain.model.Gender; 
// Enum del dominio que define el género del usuario

import com.groupsoft.piedrazul.user.domain.model.Role; 
// Enum del dominio que define el rol del usuario (ej. PATIENT, ADMIN)

import com.groupsoft.piedrazul.user.domain.model.User; 
// Entidad del dominio que representa un usuario

import com.groupsoft.piedrazul.user.domain.Repository.UserRepository; 
// Repositorio JPA para acceder y persistir usuarios en la base de datos

import com.groupsoft.piedrazul.user.infrastructure.config.RabbitMQConfig; 
// Configuración de RabbitMQ, contiene nombres de colas

import org.junit.jupiter.api.BeforeEach; 
// Anotación de JUnit 5 para ejecutar un método antes de cada prueba

import org.junit.jupiter.api.Test; 
// Anotación de JUnit 5 para definir un método de prueba

import org.junit.jupiter.api.extension.ExtendWith; 
// Permite extender el comportamiento de JUnit con extensiones (ej. Mockito)

import org.mockito.InjectMocks; 
// Inyecta los mocks en la clase bajo prueba (UserService)

import org.mockito.Mock; 
// Marca un objeto como mock (dependencia simulada, ej. repositorio o RabbitTemplate)

import org.mockito.junit.jupiter.MockitoExtension; 
// Extensión de Mockito para integrarse con JUnit 5

import org.springframework.amqp.rabbit.core.RabbitTemplate; 
// Componente de Spring AMQP para enviar mensajes a RabbitMQ

import java.time.LocalDate; 
// Clase estándar de Java para manejar fechas

import java.util.Optional; 
// Clase estándar de Java para manejar valores opcionales

import static org.junit.jupiter.api.Assertions.*; 
// Métodos de aserción de JUnit (assertEquals, assertTrue, assertNotNull, etc.)

import static org.mockito.ArgumentMatchers.any; 
// Matcher de Mockito para aceptar cualquier argumento en un mock

import static org.mockito.ArgumentMatchers.eq; 
// Matcher de Mockito para verificar igualdad exacta de argumentos

import static org.mockito.Mockito.*; 
// Métodos de verificación y configuración de Mockito (when, verify, times, never, etc.)

/**
 * Pruebas unitarias para UserService.
 * Cubre la lógica Find-or-Create y la publicación de eventos (Observer) a RabbitMQ.
 */
@ExtendWith(MockitoExtension.class) // Extiende JUnit con soporte de Mockito
class UserServiceTest {

    @Mock
    private UserRepository userRepository; // Mock del repositorio de usuarios

    @Mock
    private RabbitTemplate rabbitTemplate; // Mock del componente RabbitTemplate

    @InjectMocks
    private UserService userService; // Clase bajo prueba (SUT)

    private UserWhatsAppDTO requestDTO; // DTO de entrada para pruebas
    private User existingUser;          // Usuario simulado ya existente

    @BeforeEach
    void setUp() {
        // Configuración inicial antes de cada prueba
        requestDTO = new UserWhatsAppDTO();
        requestDTO.setDocumentNumber("1061234567");
        requestDTO.setFirstName("Carlos Andres");
        requestDTO.setLastName("Caicedo Daza");
        requestDTO.setPhone("+573001234567");
        requestDTO.setGender(Gender.HOMBRE);
        requestDTO.setBirthDate(LocalDate.of(1998, 5, 10));
        requestDTO.setEmail("carlos@test.com");

        // Usuario simulado ya existente en BD
        existingUser = User.createPatientFromWhatsApp(
                "1061234567", "Carlos Andres", "Caicedo Daza",
                "+573001234567", Gender.HOMBRE,
                LocalDate.of(1998, 5, 10), "carlos@test.com");
        existingUser.setId(101L);
    }

    // ─────────────────────────────────────────────
    // Lógica Find-or-Create
    // ─────────────────────────────────────────────

    @Test
    void shouldReturnExistingUserWhenFoundByDocument() {
        // Arrange - usuario encontrado por documento
        when(userRepository.findByDocumentNumber("1061234567"))
                .thenReturn(Optional.of(existingUser));

        // Act
        User result = userService.registerOrGetUserFromWhatsApp(requestDTO);

        // Assert - retorna usuario existente, no guarda duplicado
        assertEquals(101L, result.getId());
        assertEquals("Carlos Andres Caicedo Daza", result.getFullName());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldFindByPhoneWhenDocumentNotFound() {
        // Arrange - documento no existe pero sí el teléfono
        when(userRepository.findByDocumentNumber(anyString()))
                .thenReturn(Optional.empty());
        when(userRepository.findByPhone("+573001234567"))
                .thenReturn(Optional.of(existingUser));

        // Act
        User result = userService.registerOrGetUserFromWhatsApp(requestDTO);

        // Assert - retorna usuario existente por teléfono
        assertEquals(101L, result.getId());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldCreateAndSaveNewUserWhenNotFoundByDocumentOrPhone() {
        // Arrange - paciente completamente nuevo
        when(userRepository.findByDocumentNumber(anyString()))
                .thenReturn(Optional.empty());
        when(userRepository.findByPhone(anyString()))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        User result = userService.registerOrGetUserFromWhatsApp(requestDTO);

        // Assert - se crea y guarda nuevo usuario
        assertNotNull(result);
        assertEquals(Role.PATIENT, result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void newUserShouldHaveRolePatientAndBeActive() {
        // Arrange - nuevo usuario sin documento ni teléfono en BD
        when(userRepository.findByDocumentNumber(anyString()))
                .thenReturn(Optional.empty());
        when(userRepository.findByPhone(anyString()))
                .thenReturn(Optional.empty());

        User savedUser = User.createPatientFromWhatsApp(
                "1061234567", "Carlos Andres", "Caicedo Daza",
                "+573001234567", Gender.HOMBRE, null, null);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.registerOrGetUserFromWhatsApp(requestDTO);

        // Assert - nuevo usuario debe ser paciente y estar activo
        assertEquals(Role.PATIENT, result.getRole());
        assertTrue(result.isActive());
    }

    // ─────────────────────────────────────────────
    // Patrón Observer – publicación a RabbitMQ
    // ─────────────────────────────────────────────

    @Test
    void shouldAlwaysPublishEventToRabbitMQRegardlessOfFindOrCreate() {
        // Arrange - usuario ya existe
        when(userRepository.findByDocumentNumber(anyString()))
                .thenReturn(Optional.of(existingUser));

        // Act
        userService.registerOrGetUserFromWhatsApp(requestDTO);

        // Assert - el evento debe publicarse siempre (Observer)
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq(RabbitMQConfig.WHATSAPP_QUEUE), eq(requestDTO));
    }

    @Test
    void shouldPublishEventWithCorrectQueueWhenNewUser() {
        // Arrange - usuario nuevo
        when(userRepository.findByDocumentNumber(anyString()))
                .thenReturn(Optional.empty());
        when(userRepository.findByPhone(anyString()))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.registerOrGetUserFromWhatsApp(requestDTO);

        // Assert - cola correcta de RabbitMQ
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq(RabbitMQConfig.WHATSAPP_QUEUE), any(UserWhatsAppDTO.class));
    }
}
