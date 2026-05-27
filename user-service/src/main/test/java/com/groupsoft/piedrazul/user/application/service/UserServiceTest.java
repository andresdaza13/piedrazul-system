package com.groupsoft.piedrazul.user.application.service;

import com.groupsoft.piedrazul.user.application.dto.UserWhatsAppDTO;
import com.groupsoft.piedrazul.user.domain.model.Gender;
import com.groupsoft.piedrazul.user.domain.model.Role;
import com.groupsoft.piedrazul.user.domain.model.User;
import com.groupsoft.piedrazul.user.domain.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserWhatsAppDTO requestDTO;
    private User existingUser;

    @BeforeEach
    void setUp() {
        // Configuramos el DTO de entrada
        requestDTO = new UserWhatsAppDTO();
        requestDTO.setDocumentNumber("1061234567");
        requestDTO.setFirstName("Carlos Andres");
        requestDTO.setLastName("Caicedo Daza");
        requestDTO.setPhone("+573001234567");
        requestDTO.setGender(Gender.HOMBRE);
        
        // Configuramos una respuesta simulada de la BD
        existingUser = User.createPatientFromWhatsApp(
                "1061234567", "Carlos Andres", "Caicedo Daza", "+573001234567",
                Gender.HOMBRE, LocalDate.of(1998, 5, 10), "carlos@test.com"
        );
        existingUser.setId(101L); // Simulamos que Hibernate ya le dio un ID
    }

    @Test
    void shouldReturnExistingUserWhenDocumentIsFound() {
        // Arrange: Simulamos que la BD encuentra al paciente por documento
        when(userRepository.findByDocumentNumber(requestDTO.getDocumentNumber()))
                .thenReturn(Optional.of(existingUser));

        // Act
        User result = userService.registerOrGetUserFromWhatsApp(requestDTO);

        // Assert
        assertEquals(101L, result.getId());
        assertEquals("Carlos Andres Caicedo Daza", result.getFullName());
        // Verificamos que NUNCA se llamó al método save()
        verify(userRepository, never()).save(any(User.class)); 
    }

    @Test
    void shouldCreateAndSaveNewUserWhenNotFound() {
        // Arrange: Simulamos que la BD NO encuentra al paciente
        when(userRepository.findByDocumentNumber(requestDTO.getDocumentNumber()))
                .thenReturn(Optional.empty());
        when(userRepository.findByPhone(requestDTO.getPhone()))
                .thenReturn(Optional.empty());
        
        // Simulamos el comportamiento del guardado
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        User result = userService.registerOrGetUserFromWhatsApp(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(Role.PATIENT, result.getRole());
        // Verificamos que se haya llamado al método save() exactamente 1 vez
        verify(userRepository, times(1)).save(any(User.class));
    }
}