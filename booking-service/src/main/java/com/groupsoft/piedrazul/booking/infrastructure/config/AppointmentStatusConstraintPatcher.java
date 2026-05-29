package com.groupsoft.piedrazul.booking.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Actualiza el CHECK de PostgreSQL para incluir RESCHEDULED.
 * Hibernate ddl-auto=update no modifica restricciones existentes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentStatusConstraintPatcher {

    private final JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void patchStatusConstraint() {
        try {
            jdbcTemplate.execute(
                    "ALTER TABLE appointments DROP CONSTRAINT IF EXISTS appointments_status_check");
            jdbcTemplate.execute("""
                    ALTER TABLE appointments ADD CONSTRAINT appointments_status_check
                    CHECK (status IN ('PENDING','CONFIRMED','CANCELLED','COMPLETED','RESCHEDULED'))
                    """);
            log.info("Restriccion appointments_status_check actualizada con estado RESCHEDULED");
        } catch (Exception ex) {
            log.warn("No se pudo actualizar appointments_status_check: {}", ex.getMessage());
        }
    }
}
