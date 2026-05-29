package com.groupsoft.piedrazul.booking.domain.state;

import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;

public class RescheduledAppointmentState implements AppointmentState {

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.RESCHEDULED;
    }

    @Override
    public void ensureCanReschedule() {
        // Permitido: seguimiento medico puede re-agendar nuevamente
    }
}
