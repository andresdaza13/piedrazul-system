package com.groupsoft.piedrazul.booking.domain.Repository;

import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorIdAndAppointmentDateBetween(
            Long doctorId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Appointment> findByPatientId(Long patientId);

    boolean existsByDoctorIdAndAppointmentDate(
            Long doctorId, LocalDateTime appointmentDate);
}