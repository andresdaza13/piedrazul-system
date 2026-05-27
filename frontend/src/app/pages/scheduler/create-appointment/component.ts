import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserService } from '../../../core/services/user/service';
import { AppointmentService } from '../../../core/services/appointment/service';
import { AvailabilityService, DoctorDTO } from '../../../core/services/availability/service';

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

    this.userService.registerFromWhatsApp(this.patient).subscribe({
      next: (response) => {
        const patientId = response.patientId;
        const dateTime = `${this.appointment.date}T${this.appointment.time}`;

        this.appointmentService.createAppointment({
          patientId: patientId,
          doctorId: this.appointment.doctorId!,
          appointmentDate: dateTime,
          whatsappNumber: this.patient.phone,
          notes: ''
        }).subscribe({
          next: () => {
            this.success = true;
            this.loading = false;
          },
          error: (err) => {
            this.errorMessage = 'Error al crear la cita. Intente de nuevo.';
            this.loading = false;
          }
        });
      },
      error: () => {
        this.errorMessage = 'Error al registrar el paciente.';
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