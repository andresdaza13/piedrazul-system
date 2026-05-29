package com.groupsoft.piedrazul.booking.domain.state;

import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;

public class CompletedState extends AppointmentState {

    public CompletedState(Appointment appointment) {
        super(appointment);
    }

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.COMPLETED;
    }
}