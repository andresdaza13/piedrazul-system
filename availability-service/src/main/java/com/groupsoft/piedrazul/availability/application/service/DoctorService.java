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

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @Transactional
    public DoctorResponseDTO createDoctor(DoctorRequestDTO request) {
        Doctor doctor = Doctor.builder()
                .fullName(request.getFullName())
                .specialty(request.getSpecialty())
                .active(true)
                .build();
        return toDTO(doctorRepository.save(doctor));
    }

    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DoctorResponseDTO getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado con id: " + id));
    }

    private DoctorResponseDTO toDTO(Doctor doctor) {
        return DoctorResponseDTO.builder()
                .id(doctor.getId())
                .fullName(doctor.getFullName())
                .specialty(doctor.getSpecialty())
                .active(doctor.isActive())
                .build();
    }
}