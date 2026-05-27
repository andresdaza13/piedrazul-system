import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AvailabilityService, DoctorDTO } from '../../../core/services/availability/service';

@Component({
  selector: 'app-configure-availability',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './component.html'
})
export class ConfigureAvailabilityComponent implements OnInit {
  doctors: DoctorDTO[] = [];
  loading: boolean = false;
  success: boolean = false;
  errorMessage: string = '';

  daysOfWeek = [
    { value: 'MONDAY', label: 'Lunes' },
    { value: 'TUESDAY', label: 'Martes' },
    { value: 'WEDNESDAY', label: 'Miercoles' },
    { value: 'THURSDAY', label: 'Jueves' },
    { value: 'FRIDAY', label: 'Viernes' },
    { value: 'SATURDAY', label: 'Sabado' },
    { value: 'SUNDAY', label: 'Domingo' }
  ];

  config = {
    doctorId: null as number | null,
    dayOfWeek: '',
    startTime: '',
    endTime: '',
    intervalMinutes: 30
  };

  constructor(private availabilityService: AvailabilityService) {}

  ngOnInit() {
    this.availabilityService.getDoctors().subscribe(doctors => {
      this.doctors = doctors;
    });
  }

  save() {
    this.loading = true;
    this.errorMessage = '';
    this.success = false;

    this.availabilityService.configureAvailability({
      doctorId: this.config.doctorId!,
      dayOfWeek: this.config.dayOfWeek,
      startTime: this.config.startTime + ':00',
      endTime: this.config.endTime + ':00',
      intervalMinutes: this.config.intervalMinutes
    }).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Error al guardar la configuracion.';
        this.loading = false;
      }
    });
  }
}