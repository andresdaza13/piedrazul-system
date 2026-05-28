package com.groupsoft.piedrazul.user.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreatePatientFromWhatsAppWithCorrectDefaultValues() {
        // Arrange (Preparar datos)
        String document = "1061234567";
        String firstName = "Carlos Andres";
        String lastName = "Caicedo Daza";
        String phone = "+573001234567";
        Gender gender = Gender.HOMBRE;
        LocalDate birthDate = LocalDate.of(1998, 5, 10);
        String email = "carlos@test.com";

        // Act (Ejecutar el método a probar)
        User user = User.createPatientFromWhatsApp(
                document, firstName, lastName, phone, gender, birthDate, email
        );

        // Assert (Verificar los resultados)
        assertEquals("Carlos Andres Caicedo Daza", user.getFullName(), "El nombre completo debe concatenarse correctamente");
        assertEquals(document, user.getDocumentNumber());
        assertEquals(document, user.getUsername(), "El username temporal debe ser el documento");
        assertEquals(phone, user.getPhone());
        assertEquals(Gender.HOMBRE, user.getGender());
        assertEquals(Role.PATIENT, user.getRole(), "El rol por defecto debe ser PATIENT");
        assertTrue(user.isActive(), "El usuario debe nacer activo");
        assertNotNull(user.getPassword(), "Se debe haber autogenerado una contraseña UUID");
    }

    @Test
    void shouldGenerateRandomPassword() {

        User user1 = User.createPatientFromWhatsApp(
                "1", "A", "B", "123",
                Gender.HOMBRE,
                LocalDate.now(),
                "a@test.com"
        );

        User user2 = User.createPatientFromWhatsApp(
                "2", "C", "D", "456",
                Gender.HOMBRE,
                LocalDate.now(),
                "b@test.com"
        );

        assertNotEquals(user1.getPassword(), user2.getPassword());
    }

    
}