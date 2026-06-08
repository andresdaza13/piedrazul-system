package com.groupsoft.piedrazul.availability.domain.repository;

import com.groupsoft.piedrazul.availability.domain.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

// @Repository: indica que esta interfaz es un componente de acceso a datos.
// Extiende JpaRepository, lo que provee automáticamente operaciones CRUD
// (create, read, update, delete) sobre la entidad Availability.
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    
    /**
     * Consulta todas las disponibilidades activas de un médico.
     * @param doctorId Identificador del médico.
     * @return Lista de disponibilidades activas.
     */
    List<Availability> findByDoctorIdAndActiveTrue(Long doctorId);
    
    /**
     * Consulta la disponibilidad activa de un médico en un día específico.
     * @param doctorId Identificador del médico.
     * @param dayOfWeek Día de la semana.
     * @return Optional con la disponibilidad encontrada (vacío si no existe).
     */
    Optional<Availability> findByDoctorIdAndDayOfWeekAndActiveTrue(Long doctorId, DayOfWeek dayOfWeek);
}
