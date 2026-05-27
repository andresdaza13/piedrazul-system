package com.groupsoft.piedrazul.user.application.service;

import com.groupsoft.piedrazul.user.application.dto.UserWhatsAppDTO;
import com.groupsoft.piedrazul.user.domain.model.Gender;
import com.groupsoft.piedrazul.user.domain.model.Role;
import com.groupsoft.piedrazul.user.domain.model.User;
import com.groupsoft.piedrazul.user.domain.Repository.UserRepository;
import com.groupsoft.piedrazul.user.infrastructure.config.RabbitMQConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para UserService.
 * Cubre la lógica Find-or-Create y la publicación de eventos (Observer) a RabbitMQ.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private UserService userService;

    private UserWhatsAppDTO requestDTO;
    private User existingUser;

    @BeforeEach
    void setUp() {
        requestDTO = new UserWhatsAppDTO();
        requestDTO.setDocumentNumber("1061234567");
        requestDTO.setFirstName("Carlos Andres");
        requestDTO.setLastName("Caicedo Daza");
        requestDTO.setPhone("+573001234567");
        requestDTO.setGender(Gender.HOMBRE);
        requestDTO.setBirthDate(LocalDate.of(1998, 5, 10));
        requestDTO.setEmail("carlos@test.com");

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
        // Arrange
        when(userRepository.findByDocumentNumber("1061234567"))
                .thenReturn(Optional.of(existingUser));

        // Act
        User result = userService.registerOrGetUserFromWhatsApp(requestDTO);

        // Assert
        assertEquals(101L, result.getId());
        assertEquals("Carlos Andres Caicedo Daza", result.getFullName());
        // Nunca debe guardarse un duplicado
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

        // Assert
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

        // Assert
        assertNotNull(result);
        assertEquals(Role.PATIENT, result.getRole());
        // save() debe haberse llamado exactamente una vez
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void newUserShouldHaveRolePatientAndBeActive() {
        // Arrange
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

        // Assert
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
