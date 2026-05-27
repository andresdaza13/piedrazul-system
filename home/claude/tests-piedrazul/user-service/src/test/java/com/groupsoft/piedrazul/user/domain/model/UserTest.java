package com.groupsoft.piedrazul.user.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad User.
 * Cubre el patrón Factory Method: User.createPatientFromWhatsApp()
 */
class UserTest {

    @Test
    void shouldCreatePatientFromWhatsAppWithCorrectDefaultValues() {
        // Arrange
        String document  = "1061234567";
        String firstName = "Carlos Andres";
        String lastName  = "Caicedo Daza";
        String phone     = "+573001234567";
        Gender gender    = Gender.HOMBRE;
        LocalDate birthDate = LocalDate.of(1998, 5, 10);
        String email     = "carlos@test.com";

        // Act
        User user = User.createPatientFromWhatsApp(
                document, firstName, lastName, phone, gender, birthDate, email);

        // Assert
        assertEquals("Carlos Andres Caicedo Daza", user.getFullName(),
                "El nombre completo debe concatenarse correctamente");
        assertEquals(document, user.getDocumentNumber());
        assertEquals(document, user.getUsername(),
                "El username temporal debe ser el número de documento");
        assertEquals(phone, user.getPhone());
        assertEquals(Gender.HOMBRE, user.getGender());
        assertEquals(Role.PATIENT, user.getRole(),
                "El rol por defecto debe ser PATIENT");
        assertTrue(user.isActive(), "El usuario debe nacer activo");
        assertNotNull(user.getPassword(),
                "Se debe haber autogenerado una contraseña UUID");
    }

    @Test
    void shouldTrimSpacesInFirstAndLastName() {
        // Arrange - nombres con espacios extra
        User user = User.createPatientFromWhatsApp(
                "999", "  Ana  ", "  Pérez  ",
                "3001111111", Gender.MUJER, null, null);

        // Assert - trim aplicado correctamente
        assertEquals("Ana Pérez", user.getFullName());
    }

    @Test
    void shouldAllowNullBirthDateAndEmail() {
        // Arrange - campos opcionales nulos
        User user = User.createPatientFromWhatsApp(
                "888", "Luis", "Gómez",
                "3009999999", Gender.HOMBRE, null, null);

        // Assert - no debe lanzar excepción
        assertNull(user.getBirthDate());
        assertNull(user.getEmail());
        assertNotNull(user.getPassword());
    }

    @Test
    void shouldGenerateUniquePasswordsForDifferentPatients() {
        // Arrange
        User user1 = User.createPatientFromWhatsApp(
                "111", "A", "B", "300", Gender.HOMBRE, null, null);
        User user2 = User.createPatientFromWhatsApp(
                "222", "C", "D", "301", Gender.MUJER, null, null);

        // Assert - las contraseñas UUID deben ser distintas
        assertNotEquals(user1.getPassword(), user2.getPassword(),
                "Cada paciente debe tener una contraseña UUID única");
    }

    @Test
    void shouldSetRolePatientNeverAdmin() {
        // Arrange & Act
        User user = User.createPatientFromWhatsApp(
                "333", "Test", "User", "302", Gender.OTRO, null, null);

        // Assert - nunca puede ser ADMINISTRATOR ni DOCTOR
        assertNotEquals(Role.ADMINISTRATOR, user.getRole());
        assertNotEquals(Role.DOCTOR, user.getRole());
        assertNotEquals(Role.SCHEDULER, user.getRole());
        assertEquals(Role.PATIENT, user.getRole());
    }

    @Test
    void builderShouldCreateUserWithAllFields() {
        // Verifica que el Builder de Lombok funciona correctamente
        User user = User.builder()
                .username("scheduler1")
                .password("pass123")
                .fullName("Sandra López")
                .documentNumber("456")
                .phone("3002222222")
                .role(Role.SCHEDULER)
                .active(true)
                .build();

        assertEquals("scheduler1", user.getUsername());
        assertEquals(Role.SCHEDULER, user.getRole());
        assertTrue(user.isActive());
    }
}
