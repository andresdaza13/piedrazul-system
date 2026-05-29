package com.groupsoft.piedrazul.booking.domain.model;

import com.groupsoft.piedrazul.booking.domain.state.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(name = "whatsapp_number", length = 20)
    private String whatsappNumber;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ===== STATE PATTERN =====
    // No se persiste en BD, se reconstruye desde el enum status
    @Transient
    private AppointmentState state;

    // Reconstruye el estado al cargar la cita desde BD
    @PostLoad
    public void initializeState() {
        this.state = createStateFromStatus();
    }

    private AppointmentState createStateFromStatus() {
        if (status == null) return new PendingState(this);
        return switch (status) {
            case PENDING     -> new PendingState(this);
            case CONFIRMED   -> new ConfirmedState(this);
            case IN_PROGRESS -> new InProgressState(this);
            case COMPLETED   -> new CompletedState(this);
            case CANCELLED   -> new CancelledState(this);
            case NO_SHOW     -> new NoShowState(this);
        };
    }

    // Metodos que delegan al estado actual
    public void confirm()    { ensureState(); state.confirm(); }
    public void start()      { ensureState(); state.start(); }
    public void complete()   { ensureState(); state.complete(); }
    public void cancel()     { ensureState(); state.cancel(); }
    public void markNoShow() { ensureState(); state.markNoShow(); }

    private void ensureState() {
        if (state == null) initializeState();
    }
}