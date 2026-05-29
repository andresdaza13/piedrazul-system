// PendingState.java
package com.groupsoft.piedrazul.booking.domain.state;

import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;

public class PendingState extends AppointmentState {

    public PendingState(Appointment appointment) {
        super(appointment);
    }

    @Override
    public void confirm() {
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setState(new ConfirmedState(appointment));
        System.out.println("Cita " + appointment.getId() + " CONFIRMADA");
    }

    @Override
    public void cancel() {
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setState(new CancelledState(appointment));
        System.out.println("Cita " + appointment.getId() + " CANCELADA");
    }

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.PENDING;
    }
}