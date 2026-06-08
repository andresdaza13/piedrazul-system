package com.groupsoft.piedrazul.booking.domain.Repository;

import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

// @Repository: indica que esta interfaz es un componente de acceso a datos.
// Extiende JpaRepository, lo que provee métodos CRUD (findAll, save, delete, etc.)
// sin necesidad de implementarlos manualmente.
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Consulta todas las citas de un médico en un rango de tiempo específico.
     * Se usa para listar citas por fecha (ej. requisito funcional 1).
     *
     * @param doctorId Identificador del médico.
     * @param startOfDay Inicio del rango (00:00).
     * @param endOfDay Fin del rango (23:59).
     * @return Lista de citas en ese rango.
     */
    List<Appointment> findByDoctorIdAndAppointmentDateBetween(
            Long doctorId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    /**
     * Consulta todas las citas asociadas a un paciente.
     * Útil para mostrar historial de citas de un paciente.
     *
     * @param patientId Identificador del paciente.
     * @return Lista de citas del paciente.
     */
    List<Appointment> findByPatientId(Long patientId);

    /**
     * Verifica si un médico ya tiene una cita en una fecha/hora específica.
     * Se usa para validar conflictos de horario antes de crear una cita.
     *
     * @param doctorId Identificador del médico.
     * @param appointmentDate Fecha y hora de la cita.
     * @return true si existe una cita en ese horario, false en caso contrario.
     */
    boolean existsByDoctorIdAndAppointmentDate(
            Long doctorId, LocalDateTime appointmentDate);
}
