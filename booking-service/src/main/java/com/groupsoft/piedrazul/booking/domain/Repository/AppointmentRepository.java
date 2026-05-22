package com.groupsoft.piedrazul.booking.domain.repository;

import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Cumple el requisito de listar citas de un médico en una fecha específica (rango de horas del día)
    List<Appointment> findByDoctorIdAndDateTimeBetween(Long doctorId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    // Útil para listar todas las citas que agendó un paciente en particular a través de la web
    List<Appointment> findByPatientUserId(Long patientUserId);
}