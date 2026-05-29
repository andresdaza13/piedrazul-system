// InProgressState.java
package com.groupsoft.piedrazul.booking.domain.state;
import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;

public class InProgressState extends AppointmentState {

    public InProgressState(Appointment appointment) {
        super(appointment);
    }

    @Override
    public void complete() {
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setState(new CompletedState(appointment));
    }

    @Override
    public void cancel() {
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setState(new CancelledState(appointment));
    }

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.IN_PROGRESS;
    }
}