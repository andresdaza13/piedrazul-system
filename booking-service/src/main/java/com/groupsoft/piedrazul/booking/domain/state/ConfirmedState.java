
// ConfirmedState.java
package com.groupsoft.piedrazul.booking.domain.state;

import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;

public class ConfirmedState extends AppointmentState {

    public ConfirmedState(Appointment appointment) {
        super(appointment);
    }

    @Override
    public void start() {
        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        appointment.setState(new InProgressState(appointment));
    }

    @Override
    public void cancel() {
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setState(new CancelledState(appointment));
    }

    @Override
    public void markNoShow() {
        appointment.setStatus(AppointmentStatus.NO_SHOW);
        appointment.setState(new NoShowState(appointment));
    }

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.CONFIRMED;
    }
}