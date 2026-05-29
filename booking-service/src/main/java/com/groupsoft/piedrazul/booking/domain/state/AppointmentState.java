package com.groupsoft.piedrazul.booking.domain.state;

import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;

/**
 * PATRON STATE (Comportamiento): reglas de transicion segun el estado de la cita.
 */
public interface AppointmentState {

    AppointmentStatus getStatus();

    void ensureCanReschedule();
}
