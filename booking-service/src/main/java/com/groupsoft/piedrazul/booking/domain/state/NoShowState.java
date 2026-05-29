package com.groupsoft.piedrazul.booking.domain.state;

import com.groupsoft.piedrazul.booking.domain.model.Appointment;
import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;

public class NoShowState extends AppointmentState {

    public NoShowState(Appointment appointment) {
        super(appointment);
    }

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.NO_SHOW;
    }
}