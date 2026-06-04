import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
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
    this.availabilityService.getDoctors().subscribe({
      next: (doctors) => {
        this.doctors = doctors;
      },
      error: () => {
        this.errorMessage = 'No se pudo cargar la lista de medicos. Verifique que availability-service este activo.';
      }
    });
  }

  createDoctor() {
    this.loading = true;
    this.errorMessage = '';
    this.success = false;

    this.availabilityService.createDoctor(this.newDoctor).pipe(
      finalize(() => { this.loading = false; })
    ).subscribe({
      next: () => {
        this.success = true;
        this.newDoctor = { fullName: '', specialty: '' };
        this.loadDoctors();
      },
      error: () => {
        this.errorMessage = 'Error al crear el medico.';
      }
    });
  }
}