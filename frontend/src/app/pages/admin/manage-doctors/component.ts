import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AvailabilityService, DoctorDTO } from '../../../core/services/availability/service';

@Component({
  selector: 'app-manage-doctors',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './component.html'
})
export class ManageDoctorsComponent implements OnInit {
  doctors: DoctorDTO[] = [];
  loading: boolean = false;
  success: boolean = false;
  errorMessage: string = '';

  newDoctor = {
    fullName: '',
    specialty: ''
  };

  constructor(private availabilityService: AvailabilityService) {}

  ngOnInit() {
    this.loadDoctors();
  }

  loadDoctors() {
    this.availabilityService.getDoctors().subscribe(doctors => {
      this.doctors = doctors;
    });
  }

  createDoctor() {
    this.loading = true;
    this.errorMessage = '';
    this.success = false;
    this.availabilityService.createDoctor(this.newDoctor).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
        this.newDoctor = { fullName: '', specialty: '' };
        this.loadDoctors();
      },
      error: () => {
        this.errorMessage = 'Error al crear el medico.';
        this.loading = false;
      }
    });
  }
}