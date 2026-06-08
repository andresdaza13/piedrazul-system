import { Component, OnInit } from '@angular/core'; // Decorador para definir un componente Angular
import { CommonModule } from '@angular/common';    // Módulo común de Angular (ngIf, ngFor, etc.)
import { FormsModule } from '@angular/forms';      // Módulo para formularios template-driven
import { RouterLink } from '@angular/router';      // Permite navegación con enlaces de router
import { UserService } from '../../../core/services/user/service'; // Servicio de usuarios
import { AppointmentService } from '../../../core/services/appointment/service'; // Servicio de citas
import { AvailabilityService, DoctorDTO } from '../../../core/services/availability/service'; // Servicio de disponibilidad y DTO de médicos

@Component({
  selector: 'app-create-appointment', // Nombre del selector para usar el componente en HTML
  standalone: true,                   // Componente independiente (no necesita declararse en un módulo)
  imports: [CommonModule, FormsModule, RouterLink], // Módulos que el componente utiliza
  templateUrl: './component.html'     // Plantilla HTML asociada
})
export class CreateAppointmentComponent implements OnInit {
  doctors: DoctorDTO[] = [];         // Lista de médicos obtenida del backend
  availableSlots: string[] = [];     // Lista de franjas horarias disponibles
  loading: boolean = false;          // Estado de carga para mostrar spinner o deshabilitar botones
  success: boolean = false;          // Estado de éxito al crear cita
  errorMessage: string = '';         // Mensaje de error en caso de fallo

  // Datos del paciente a registrar
  patient = {
    documentNumber: '',
    firstName: '',
    lastName: '',
    phone: '',
    gender: '',
    birthDate: '',
    email: ''
  };

  // Datos de la cita a crear
  appointment = {
    doctorId: null as number | null, // ID del médico seleccionado
    date: '',                        // Fecha de la cita
    time: ''                         // Hora de la cita
  };

  // Inyección de servicios
  constructor(
    private userService: UserService,
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

  // Método que se ejecuta cuando cambia el médico o la fecha
  onDoctorOrDateChange() {
    if (this.appointment.doctorId && this.appointment.date) {
      this.availabilityService.getAvailableSlots(
        this.appointment.doctorId,
        this.appointment.date
      ).subscribe(slots => {
        this.availableSlots = slots; // Actualiza las franjas disponibles
        this.appointment.time = '';  // Reinicia la hora seleccionada
      });
    }
  }

  // Método para enviar el formulario y crear la cita
  submit() {
    this.loading = true;
    this.errorMessage = '';

    // Primero registra al paciente
    this.userService.registerFromWhatsApp(this.patient).subscribe({
      next: (response) => {
        const patientId = response.patientId; // Obtiene el ID del paciente registrado
        const dateTime = `${this.appointment.date}T${this.appointment.time}`; // Construye fecha y hora en formato ISO

        // Luego crea la cita con los datos del paciente y médico
        this.appointmentService.createAppointment({
          patientId: patientId,
          doctorId: this.appointment.doctorId!,
          appointmentDate: dateTime,
          whatsappNumber: this.patient.phone,
          notes: ''
        }).subscribe({
          next: () => {
            this.success = true;   // Marca éxito
            this.loading = false;  // Desactiva estado de carga
          },
          error: (err) => {
            this.errorMessage = 'Error al crear la cita. Intente de nuevo.'; // Mensaje de error
            this.loading = false;
          }
        });
      },
      error: () => {
        this.errorMessage = 'Error al registrar el paciente.'; // Mensaje de error si falla el registro
        this.loading = false;
      }
    });
  }

  // Método para reiniciar el formulario
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
