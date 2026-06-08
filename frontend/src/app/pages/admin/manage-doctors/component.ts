import { Component, OnInit } from '@angular/core'; // Decorador para definir un componente Angular
import { CommonModule } from '@angular/common';    // Módulo común de Angular (ngIf, ngFor, etc.)
import { FormsModule } from '@angular/forms';      // Módulo para formularios template-driven
import { RouterLink } from '@angular/router';      // Permite navegación con enlaces de router
import { AvailabilityService, DoctorDTO } from '../../../core/services/availability/service'; // Servicio de disponibilidad y DTO de médicos

@Component({
  selector: 'app-manage-doctors', // Nombre del selector para usar el componente en HTML
  standalone: true,               // Componente independiente (no necesita declararse en un módulo)
  imports: [CommonModule, FormsModule, RouterLink], // Módulos que el componente utiliza
  templateUrl: './component.html' // Plantilla HTML asociada
})
export class ManageDoctorsComponent implements OnInit {
  doctors: DoctorDTO[] = []; // Lista de médicos obtenida del backend
  loading: boolean = false;  // Estado de carga para mostrar spinner o deshabilitar botones
  success: boolean = false;  // Estado de éxito al crear médico
  errorMessage: string = ''; // Mensaje de error en caso de fallo

  // Objeto para capturar datos del nuevo médico
  newDoctor = {
    fullName: '',   // Nombre completo
    specialty: ''   // Especialidad
  };

  // Inyección del servicio de disponibilidad
  constructor(private availabilityService: AvailabilityService) {}

  // Método que se ejecuta al inicializar el componente
  ngOnInit() {
    this.loadDoctors(); // Carga la lista de médicos al iniciar
  }

  // Método para obtener la lista de médicos desde el backend
  loadDoctors() {
    this.availabilityService.getDoctors().subscribe(doctors => {
      this.doctors = doctors;
    });
  }

  // Método para crear un nuevo médico
  createDoctor() {
    this.loading = true;       // Activa estado de carga
    this.errorMessage = '';    // Limpia mensaje de error
    this.success = false;      // Reinicia estado de éxito

    // Llama al servicio para crear un médico
    this.availabilityService.createDoctor(this.newDoctor).subscribe({
      next: () => {
        this.success = true;   // Marca éxito
        this.loading = false;  // Desactiva estado de carga
        this.newDoctor = { fullName: '', specialty: '' }; // Limpia formulario
        this.loadDoctors();    // Recarga lista de médicos
      },
      error: () => {
        this.errorMessage = 'Error al crear el medico.'; // Mensaje de error
        this.loading = false;  // Desactiva estado de carga
      }
    });
  }
}
