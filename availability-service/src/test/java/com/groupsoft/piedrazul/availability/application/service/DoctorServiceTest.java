package com.groupsoft.piedrazul.availability.application.service; // Paquete donde se ubica la clase de prueba

// Importa los DTOs usados para la comunicación entre capas
import com.groupsoft.piedrazul.availability.application.dto.DoctorRequestDTO;
import com.groupsoft.piedrazul.availability.application.dto.DoctorResponseDTO;

// Importa la entidad del dominio
import com.groupsoft.piedrazul.availability.domain.model.Doctor;

// Importa el repositorio de persistencia
import com.groupsoft.piedrazul.availability.domain.repository.DoctorRepository;

// Importa librerías de JUnit para pruebas unitarias
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Importa librerías de Mockito para simular dependencias
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Importa utilidades de colecciones
import java.util.List;
import java.util.Optional;

// Importa métodos estáticos para simplificar aserciones y verificaciones
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Extiende la clase de prueba con soporte de Mockito
@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    // Se usa un mock para simular el repositorio de médicos
    @Mock
    private DoctorRepository doctorRepository;

    // Inyecta el mock en el servicio bajo prueba (SUT)
    @InjectMocks
    private DoctorService doctorService;

    // Objetos de prueba
    private Doctor doctor; // Entidad Doctor simulada
    private DoctorRequestDTO request; // DTO de entrada para crear un doctor

    @BeforeEach
    void setUp() {
        // Inicializa datos de prueba: un doctor activo con especialidad
        doctor = Doctor.builder()
                .id(1L)
                .fullName("Carlos Ruiz")
                .specialty("Dermatologia")
                .active(true)
                .build();

        // DTO de entrada para crear un doctor
        request = new DoctorRequestDTO();
        request.setFullName("Carlos Ruiz");
        request.setSpecialty("Dermatologia");
    }

    @Test
    void shouldCreateDoctorSuccessfully() {
        // Simula que el repositorio guarda el doctor correctamente
        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(doctor);

        // Ejecuta el método y obtiene respuesta
        DoctorResponseDTO response =
                doctorService.createDoctor(request);

        // Verifica que la respuesta no sea nula y tenga los datos correctos
        assertNotNull(response);
        assertEquals("Carlos Ruiz", response.getFullName());

        // Verifica que se haya invocado el método save del repositorio
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void shouldReturnAllDoctors() {
        // Simula que el repositorio devuelve una lista con un doctor
        when(doctorRepository.findAll())
                .thenReturn(List.of(doctor));

        // Ejecuta el método
        List<DoctorResponseDTO> result =
                doctorService.getAllDoctors();

        // Verifica que la lista tenga un elemento y que la especialidad sea correcta
        assertEquals(1, result.size());
        assertEquals("Dermatologia",
                result.get(0).getSpecialty());
    }

    @Test
    void shouldReturnDoctorById() {
        // Simula que el repositorio encuentra al doctor por ID
        when(doctorRepository.findById(1L))
                .thenReturn(Optional.of(doctor));

        // Ejecuta el método
        DoctorResponseDTO response =
                doctorService.getDoctorById(1L);

        // Verifica que el ID del doctor sea el esperado
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowExceptionWhenDoctorNotFound() {
        // Simula que el repositorio no encuentra al doctor
        when(doctorRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Verifica que se lance excepción al buscar un doctor inexistente
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> doctorService.getDoctorById(1L)
        );

        // Verifica que el mensaje de la excepción contenga "Doctor no encontrado"
        assertTrue(exception.getMessage()
                .contains("Doctor no encontrado"));
    }
}
