package com.groupsoft.piedrazul.availability.domain.repository;

import com.groupsoft.piedrazul.availability.domain.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    // Busca la disponibilidad de un médico en un rango de fechas específico
    List<Availability> findByDoctorIdAndStartTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);
}