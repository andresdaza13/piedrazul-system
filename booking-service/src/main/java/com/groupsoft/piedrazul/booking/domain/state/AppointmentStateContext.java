package com.groupsoft.piedrazul.booking.domain.state;

import com.groupsoft.piedrazul.booking.domain.model.AppointmentStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AppointmentStateContext {

    private final Map<AppointmentStatus, AppointmentState> states = Map.of(
            AppointmentStatus.PENDING, new PendingAppointmentState(),
            AppointmentStatus.CONFIRMED, new PendingAppointmentState(),
            AppointmentStatus.RESCHEDULED, new RescheduledAppointmentState(),
            AppointmentStatus.CANCELLED, new TerminalAppointmentState(AppointmentStatus.CANCELLED),
            AppointmentStatus.COMPLETED, new TerminalAppointmentState(AppointmentStatus.COMPLETED)
    );

    public void ensureCanReschedule(AppointmentStatus status) {
        AppointmentState state = states.getOrDefault(
                status, new TerminalAppointmentState(status));
        state.ensureCanReschedule();
    }
}
