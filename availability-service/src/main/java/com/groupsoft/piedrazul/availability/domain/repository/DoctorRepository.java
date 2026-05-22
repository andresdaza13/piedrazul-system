package com.groupsoft.piedrazul.availability.domain.repository;

import com.groupsoft.piedrazul.availability.domain.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}