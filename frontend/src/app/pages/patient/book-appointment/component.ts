import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { AvailabilityService, DoctorDTO } from '../../../core/services/availability/service';
import { AppointmentService } from '../../../core/services/appointment/service';
import { UserService } from '../../../core/services/user/service';
import { buildBookingWindow } from '../../../core/utils/booking-window';

@Component({
  selector: 'app-book-appointment',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './component.html'
})
export class BookAppointmentComponent implements OnInit {
  step: number = 1;
  doctors: DoctorDTO[] = [];
  availableSlots: string[] = [];
  loading: boolean = false;
  success: boolean = false;
  errorMessage: string = '';
  patientId: number | null = null;
  minDate = '';
  maxDate = '';

  patient = {
    documentNumber: '', firstName: '', lastName: '',
    phone: '', gender: '', birthDate: '', email: ''
  };

  booking = {
    doctorId: null as number | null,
    date: '',
    time: ''
  };

  constructor(
    private availabilityService: AvailabilityService,
    private appointmentService: AppointmentService,
    private userService: UserService
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

  registerPatient() {
    this.loading = true;
    this.errorMessage = '';
    this.userService.registerWebPatient(this.patient).pipe(
      finalize(() => { this.loading = false; })
    ).subscribe({
      next: (response) => {
        this.patientId = response.patientId;
        this.step = 2;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Error al registrar el paciente.';
      }
    });
  }

  onDoctorOrDateChange() {
    if (this.booking.doctorId && this.booking.date) {
      this.availabilityService.getAvailableSlots(
        this.booking.doctorId,
        this.booking.date
      ).subscribe(slots => {
        this.availableSlots = slots;
        this.booking.time = '';
      });
    }
  }

  confirmBooking() {
    this.loading = true;
    this.errorMessage = '';
    this.appointmentService.createWebBooking({
      patientId: this.patientId!,
      doctorId: this.booking.doctorId!,
      appointmentDate: this.booking.date,
      appointmentTime: this.booking.time,
      notes: 'Cita agendada desde portal web'
    }).pipe(finalize(() => { this.loading = false; }))
      .subscribe({
        next: () => { this.success = true; },
        error: (err) => {
          this.errorMessage = err.error?.message || err.error?.error || 'Error al agendar la cita.';
        }
      });
  }

  reset() {
    this.step = 1;
    this.success = false;
    this.errorMessage = '';
    this.patientId = null;
    this.patient = {
      documentNumber: '', firstName: '', lastName: '',
      phone: '', gender: '', birthDate: '', email: ''
    };
    this.booking = { doctorId: null, date: '', time: '' };
    this.availableSlots = [];
  }

  getDoctorName(): string {
    const doctor = this.doctors.find(d => d.id === this.booking.doctorId);
    return doctor ? `${doctor.fullName} - ${doctor.specialty}` : '';
  }
}
