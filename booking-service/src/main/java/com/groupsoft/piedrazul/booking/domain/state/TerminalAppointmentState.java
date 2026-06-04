package com.groupsoft.piedrazul.booking.domain.state;

import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;

public class TerminalAppointmentState implements AppointmentState {

    private final AppointmentStatus status;

    public TerminalAppointmentState(AppointmentStatus status) {
        this.status = status;
    }

    @Override
    public AppointmentStatus getStatus() {
        return status;
    }

    @Override
    public void ensureCanReschedule() {
        throw new IllegalStateException(
                "No se puede re-agendar una cita en estado " + status);
    }
}
