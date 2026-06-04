package com.groupsoft.piedrazul.booking.domain.state;

import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;

public class PendingAppointmentState implements AppointmentState {

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.PENDING;
    }

    @Override
    public void ensureCanReschedule() {
        // Permitido
    }
}
