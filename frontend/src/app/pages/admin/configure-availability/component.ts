import { Component, OnInit } from '@angular/core'; // Decorador para definir un componente Angular
import { CommonModule } from '@angular/common';    // Módulo común de Angular (ngIf, ngFor, etc.)
import { FormsModule } from '@angular/forms';      // Módulo para formularios template-driven
import { RouterLink } from '@angular/router';      // Permite navegación con enlaces de router
import { AvailabilityService, DoctorDTO } from '../../../core/services/availability/service'; // Servicio de disponibilidad y DTO de médicos

@Component({
  selector: 'app-configure-availability', // Nombre del selector para usar el componente en HTML
  standalone: true,                       // Componente independiente (no necesita declararse en un módulo)
  imports: [CommonModule, FormsModule, RouterLink], // Módulos que el componente utiliza
  templateUrl: './component.html'         // Plantilla HTML asociada
})
  
export class ConfigureAvailabilityComponent implements OnInit {
  doctors: DoctorDTO[] = []; // Lista de médicos obtenida del backend
  loading: boolean = false;  // Estado de carga para mostrar spinner o deshabilitar botones
  success: boolean = false;  // Estado de éxito al guardar configuración
  errorMessage: string = ''; // Mensaje de error en caso de fallo

  // Lista de días de la semana para selección en el formulario
  daysOfWeek = [
    { value: 'MONDAY', label: 'Lunes' },
    { value: 'TUESDAY', label: 'Martes' },
    { value: 'WEDNESDAY', label: 'Miercoles' },
    { value: 'THURSDAY', label: 'Jueves' },
    { value: 'FRIDAY', label: 'Viernes' },
    { value: 'SATURDAY', label: 'Sabado' },
    { value: 'SUNDAY', label: 'Domingo' }
  ];

  // Objeto de configuración de disponibilidad
  config = {
    doctorId: null as number | null, // ID del médico seleccionado
    dayOfWeek: '',                   // Día de la semana
    startTime: '',                   // Hora de inicio
    endTime: '',                     // Hora de fin
    intervalMinutes: 30              // Intervalo en minutos entre citas
  };

  // Inyección del servicio de disponibilidad
  constructor(private availabilityService: AvailabilityService) {}

  // Método que se ejecuta al inicializar el componente
  ngOnInit() {
    // Obtiene la lista de médicos desde el backend
    this.availabilityService.getDoctors().subscribe(doctors => {
      this.doctors = doctors;
    });
  }

  // Método para guardar la configuración de disponibilidad
  save() {
    this.loading = true;       // Activa estado de carga
    this.errorMessage = '';    // Limpia mensaje de error
    this.success = false;      // Reinicia estado de éxito

    // Llama al servicio para configurar disponibilidad
    this.availabilityService.configureAvailability({
      doctorId: this.config.doctorId!,           // ID del médico (no nulo)
      dayOfWeek: this.config.dayOfWeek,          // Día de la semana
      startTime: this.config.startTime + ':00',  // Hora de inicio con segundos
      endTime: this.config.endTime + ':00',      // Hora de fin con segundos
      intervalMinutes: this.config.intervalMinutes // Intervalo en minutos
    }).subscribe({
      next: () => {
        this.success = true;   // Marca éxito
        this.loading = false;  // Desactiva estado de carga
      },
      error: () => {
        this.errorMessage = 'Error al guardar la configuracion.'; // Mensaje de error
        this.loading = false;  // Desactiva estado de carga
      }
    });
  }
}
