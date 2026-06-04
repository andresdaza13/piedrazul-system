package com.groupsoft.piedrazul.availability.application.service;

import com.groupsoft.piedrazul.availability.application.dto.DoctorRequestDTO;
import com.groupsoft.piedrazul.availability.application.dto.DoctorResponseDTO;
import com.groupsoft.piedrazul.availability.domain.model.Doctor;
import com.groupsoft.piedrazul.availability.domain.repository.DoctorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;
    private DoctorRequestDTO request;

    @BeforeEach
    void setUp() {

        doctor = Doctor.builder()
                .id(1L)
                .fullName("Carlos Ruiz")
                .specialty("Dermatologia")
                .active(true)
                .build();

        request = new DoctorRequestDTO();
        request.setFullName("Carlos Ruiz");
        request.setSpecialty("Dermatologia");
    }

    @Test
    void shouldCreateDoctorSuccessfully() {

        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(doctor);

        DoctorResponseDTO response =
                doctorService.createDoctor(request);

        assertNotNull(response);
        assertEquals("Carlos Ruiz", response.getFullName());

        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void shouldReturnAllDoctors() {

        when(doctorRepository.findAll())
                .thenReturn(List.of(doctor));

        List<DoctorResponseDTO> result =
                doctorService.getAllDoctors();

        assertEquals(1, result.size());
        assertEquals("Dermatologia",
                result.get(0).getSpecialty());
    }

    @Test
    void shouldReturnDoctorById() {

        when(doctorRepository.findById(1L))
                .thenReturn(Optional.of(doctor));

        DoctorResponseDTO response =
                doctorService.getDoctorById(1L);

        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowExceptionWhenDoctorNotFound() {

        when(doctorRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> doctorService.getDoctorById(1L)
        );

        assertTrue(exception.getMessage()
                .contains("Doctor no encontrado"));
    }
}