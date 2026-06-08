package com.groupsoft.piedrazul.availability.application.service;

import com.groupsoft.piedrazul.availability.application.dto.DoctorRequestDTO;
import com.groupsoft.piedrazul.availability.application.dto.DoctorResponseDTO;
import com.groupsoft.piedrazul.availability.domain.model.Doctor;
import com.groupsoft.piedrazul.availability.domain.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

// @Service: indica que esta clase es un componente de la capa de servicio.
// @RequiredArgsConstructor: genera automáticamente el constructor con los atributos finales.
@Service
@RequiredArgsConstructor
public class DoctorService {

    // Repositorio para acceder y persistir médicos en la base de datos.
    private final DoctorRepository doctorRepository;

    /**
     * Crear un nuevo médico en el sistema.
     * @param request DTO con los datos básicos del médico.
     * @return DTO de respuesta con el médico creado.
     */
    @Transactional
    public DoctorResponseDTO createDoctor(DoctorRequestDTO request) {
        Doctor doctor = Doctor.builder()
                .fullName(request.getFullName())
                .specialty(request.getSpecialty())
                .active(true) // Por defecto, el médico se registra como activo.
                .build();
        return toDTO(doctorRepository.save(doctor));
    }

    /**
     * Obtener todos los médicos registrados en el sistema.
     * @return Lista de médicos en formato DTO.
     */
    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener un médico específico por su ID.
     * @param id Identificador del médico.
     * @return DTO con la información del médico.
     */
    public DoctorResponseDTO getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado con id: " + id));
    }

    // Método auxiliar para transformar la entidad Doctor en un DTO de respuesta.
    private DoctorResponseDTO toDTO(Doctor doctor) {
        return DoctorResponseDTO.builder()
                .id(doctor.getId())
                .fullName(doctor.getFullName())
                .specialty(doctor.getSpecialty())
                .active(doctor.isActive())
                .build();
    }
}
