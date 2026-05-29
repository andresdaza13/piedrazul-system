package com.groupsoft.piedrazul.booking.domain.Repository;

import com.groupsoft.piedrazul.booking.domain.model.AppointmentRescheduleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRescheduleHistoryRepository
        extends JpaRepository<AppointmentRescheduleHistory, Long> {

    List<AppointmentRescheduleHistory> findByAppointmentIdOrderByChangedAtDesc(Long appointmentId);
}
