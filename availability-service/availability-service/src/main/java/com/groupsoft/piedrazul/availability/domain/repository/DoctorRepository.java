package com.groupsoft.piedrazul.availability.domain.repository;

import com.groupsoft.piedrazul.availability.domain.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// @Repository: indica que esta interfaz es un componente de acceso a datos.
// Extiende JpaRepository, lo que provee automáticamente operaciones CRUD
// (create, read, update, delete) sobre la entidad Doctor.
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    // No es necesario definir métodos adicionales aquí,
    // JpaRepository ya provee métodos como:
    // - findAll()
    // - findById(Long id)
    // - save(Doctor doctor)
    // - deleteById(Long id)
}
