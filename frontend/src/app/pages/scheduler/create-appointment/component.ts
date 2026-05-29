import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { UserService } from '../../../core/services/user/service';
import { AppointmentService } from '../../../core/services/appointment/service';
import { AvailabilityService, DoctorDTO } from '../../../core/services/availability/service';
import { buildBookingWindow } from '../../../core/utils/booking-window';
import { toLocalDateTimeIso } from '../../../core/utils/datetime';

@Component({
  selector: 'app-create-appointment',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './component.html'
})
export class CreateAppointmentComponent implements OnInit {
  doctors: DoctorDTO[] = [];
  availableSlots: string[] = [];
  loading: boolean = false;
  success: boolean = false;
  errorMessage: string = '';
  minDate = '';
  maxDate = '';

  patient = {
    documentNumber: '',
    firstName: '',
    lastName: '',
    phone: '',
    gender: '',
    birthDate: '',
    email: ''
  };

  appointment = {
    doctorId: null as number | null,
    date: '',
    time: ''
  };

  constructor(
    private userService: UserService,
    private appointmentService: AppointmentService,
    private availabilityService: AvailabilityService
  ) {}

  ngOnInit() {
    this.availabilityService.getDoctors().subscribe(doctors => {
      this.doctors = doctors;
    });
    this.availabilityService.getSystemConfig().subscribe(config => {
      const window = buildBookingWindow(config.bookingWindowWeeks);
      this.minDate = window.minDate;
      this.maxDate = window.maxDate;
    });
  }

  onDoctorOrDateChange() {
    if (this.appointment.doctorId && this.appointment.date) {
      this.availabilityService.getAvailableSlots(
        this.appointment.doctorId,
        this.appointment.date
      ).subscribe(slots => {
        this.availableSlots = slots;
        this.appointment.time = '';
      });
    }
  }

  submit() {
    this.loading = true;
    this.errorMessage = '';

    this.userService.registerFromWhatsApp(this.patient).pipe(
      finalize(() => {})
    ).subscribe({
      next: (response) => {
        const patientId = response.patientId;
        const dateTime = toLocalDateTimeIso(this.appointment.date, this.appointment.time);

        this.appointmentService.createAppointment({
          patientId: patientId,
          doctorId: this.appointment.doctorId!,
          appointmentDate: dateTime,
          whatsappNumber: this.patient.phone,
          notes: 'Cita agendada por WhatsApp'
        }).pipe(finalize(() => { this.loading = false; }))
          .subscribe({
            next: () => {
              this.success = true;
            },
            error: (err) => {
              this.errorMessage = err.error?.message || 'Error al crear la cita.';
            }
          });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Error al registrar el paciente.';
        this.loading = false;
      }
    });
  }

  reset() {
    this.success = false;
    this.patient = {
      documentNumber: '', firstName: '', lastName: '',
      phone: '', gender: '', birthDate: '', email: ''
    };
    this.appointment = { doctorId: null, date: '', time: '' };
    this.availableSlots = [];
  }
}
