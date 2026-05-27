import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AvailabilityService, DoctorDTO } from '../../../core/services/availability/service';
import { AppointmentService } from '../../../core/services/appointment/service';
import { UserService } from '../../../core/services/user/service';

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
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.availabilityService.getDoctors().subscribe(doctors => {
      this.doctors = doctors;
      this.cdr.detectChanges();
    });
  }

  registerPatient() {
    this.loading = true;
    this.errorMessage = '';
    this.userService.registerFromWhatsApp(this.patient).subscribe({
      next: (response) => {
        this.patientId = response.patientId;
        this.step = 2;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Error al registrar el paciente.';
        this.loading = false;
        this.cdr.detectChanges();
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
        this.cdr.detectChanges();
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
    }).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = err.error?.error || 'Error al agendar la cita.';
        this.loading = false;
        this.cdr.detectChanges();
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
    this.cdr.detectChanges();
  }

  getDoctorName(): string {
    const doctor = this.doctors.find(d => d.id === this.booking.doctorId);
    return doctor ? `${doctor.fullName} - ${doctor.specialty}` : '';
  }
}