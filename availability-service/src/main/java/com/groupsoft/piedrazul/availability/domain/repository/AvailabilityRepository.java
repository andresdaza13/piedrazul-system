package com.groupsoft.piedrazul.availability.domain.repository;

import com.groupsoft.piedrazul.availability.domain.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    
    List<Availability> findByDoctorIdAndActiveTrue(Long doctorId);
    
    Optional<Availability> findByDoctorIdAndDayOfWeekAndActiveTrue(Long doctorId, DayOfWeek dayOfWeek);
}