import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AppointmentService } from '../../../core/services/appointment/service';
import { AvailabilityService, DoctorDTO } from '../../../core/services/availability/service';

@Component({
  selector: 'app-appointment-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './component.html'
})
export class AppointmentListComponent implements OnInit {
  doctors: DoctorDTO[] = [];
  selectedDoctorId: number | null = null;
  selectedDate: string = '';
  appointments: any[] = [];
  total: number = 0;
  loading: boolean = false;
  searched: boolean = false;

  constructor(
    private appointmentService: AppointmentService,
    private availabilityService: AvailabilityService
  ) {}

  ngOnInit() {
    this.availabilityService.getDoctors().subscribe(doctors => {
      this.doctors = doctors;
    });
  }

  search() {
    if (!this.selectedDoctorId || !this.selectedDate) return;
    this.loading = true;
    this.searched = true;
    this.appointmentService.getAppointmentsByDoctorAndDate(
      this.selectedDoctorId, this.selectedDate
    ).subscribe({
      next: (response) => {
        this.appointments = response.appointments;
        this.total = response.total;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}