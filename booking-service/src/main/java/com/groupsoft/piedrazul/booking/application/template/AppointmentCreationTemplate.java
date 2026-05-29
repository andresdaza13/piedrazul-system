package com.groupsoft.piedrazul.booking.application.template;

import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;
import com.groupsoft.piedrazul.booking.domain.exception.AppointmentOverlapException;
import com.groupsoft.piedrazul.booking.domain.Repository.*;

public abstract class AppointmentCreationTemplate<T> {

    protected final AppointmentRepository repository;

    protected AppointmentCreationTemplate(AppointmentRepository repository) {
        this.repository = repository;
    }

    // METODO TEMPLATE — final para que las subclases no lo sobrescriban
    public final Appointment createAppointment(T request) {

        // 1. Validar peticion (cada canal valida distinto)
        validateRequest(request);

        // 2. Construir entidad (cada canal arma distinto)
        Appointment appointment = buildAppointment(request);

        // 3. Verificar disponibilidad (paso comun)
        if (isSlotOccupied(appointment)) {
            throw new AppointmentOverlapException(
                "El doctor ya tiene una cita en ese horario."
            );
        }

        // 4. Guardar (paso comun)
        appointment.setStatus(AppointmentStatus.PENDING);
        Appointment saved = repository.save(appointment);

        // 5. Notificar al canal (cada canal notifica distinto)
        notifyChannel(saved);

        return saved;
    }

    // ===== PASOS COMUNES (implementados aqui) =====
    protected boolean isSlotOccupied(Appointment appointment) {
        return repository.existsByDoctorIdAndAppointmentDate(
            appointment.getDoctorId(),
            appointment.getAppointmentDate()
        );
    }

    // ===== PASOS QUE CADA CANAL DEFINE (abstractos) =====
    protected abstract void validateRequest(T request);
    protected abstract Appointment buildAppointment(T request);
    protected abstract void notifyChannel(Appointment appointment);
}