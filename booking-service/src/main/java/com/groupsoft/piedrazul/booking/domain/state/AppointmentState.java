package com.groupsoft.piedrazul.booking.domain.state;

import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;

public abstract class AppointmentState {

    protected Appointment appointment;

    public AppointmentState(Appointment appointment) {
        this.appointment = appointment;
    }

    // Acciones posibles (cada estado decide cuales permite)
    public void confirm()      { throwNotAllowed("confirmar"); }
    public void start()        { throwNotAllowed("iniciar"); }
    public void complete()     { throwNotAllowed("completar"); }
    public void cancel()       { throwNotAllowed("cancelar"); }
    public void markNoShow()   { throwNotAllowed("marcar como no asistio"); }

    public abstract AppointmentStatus getStatus();

    protected void throwNotAllowed(String action) {
        throw new IllegalStateException(
            "No se puede " + action + " una cita en estado " + getStatus()
        );
    }
}