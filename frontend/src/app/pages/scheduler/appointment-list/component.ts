import { Component, OnInit } from '@angular/core'; // Decorador para definir un componente Angular
import { CommonModule } from '@angular/common';    // Módulo común de Angular (ngIf, ngFor, etc.)
import { FormsModule } from '@angular/forms';      // Módulo para formularios template-driven
import { RouterLink } from '@angular/router';      // Permite navegación con enlaces de router
import { AppointmentService } from '../../../core/services/appointment/service'; // Servicio de citas
import { AvailabilityService, DoctorDTO } from '../../../core/services/availability/service'; // Servicio de disponibilidad y DTO de médicos

@Component({
  selector: 'app-appointment-list', // Nombre del selector para usar el componente en HTML
  standalone: true,                 // Componente independiente (no necesita declararse en un módulo)
  imports: [CommonModule, FormsModule, RouterLink], // Módulos que el componente utiliza
  templateUrl: './component.html'   // Plantilla HTML asociada
})
export class AppointmentListComponent implements OnInit {
  doctors: DoctorDTO[] = [];         // Lista de médicos obtenida del backend
  selectedDoctorId: number | null = null; // ID del médico seleccionado
  selectedDate: string = '';         // Fecha seleccionada para filtrar citas
  appointments: any[] = [];          // Lista de citas obtenidas del backend
  total: number = 0;                 // Total de citas encontradas
  loading: boolean = false;          // Estado de carga para mostrar spinner o deshabilitar botones
  searched: boolean = false;         // Estado que indica si ya se realizó una búsqueda

  // Inyección de servicios
  constructor(
    private appointmentService: AppointmentService,
    private availabilityService: AvailabilityService
  ) {}

  // Método que se ejecuta al inicializar el componente
  ngOnInit() {
    // Obtiene la lista de médicos desde el backend
    this.availabilityService.getDoctors().subscribe(doctors => {
      this.doctors = doctors;
    });
  }

  // Método para buscar citas por médico y fecha
  search() {
    // Si no hay médico o fecha seleccionada, no hace nada
    if (!this.selectedDoctorId || !this.selectedDate) return;

    this.loading = true;   // Activa estado de carga
    this.searched = true;  // Marca que se ha realizado una búsqueda

    // Llama al servicio de citas para obtener las citas del médico en la fecha seleccionada
    this.appointmentService.getAppointmentsByDoctorAndDate(
      this.selectedDoctorId, this.selectedDate
    ).subscribe({
      next: (response) => {
        this.appointments = response.appointments; // Lista de citas obtenida
        this.total = response.total;               // Total de citas
        this.loading = false;                      // Desactiva estado de carga
      },
      error: () => this.loading = false            // En caso de error, desactiva estado de carga
    });
  }
}
