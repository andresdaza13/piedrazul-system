import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; // Decorador para definir un componente Angular y ChangeDetectorRef para refrescar la vista manualmente
import { CommonModule } from '@angular/common';    // Módulo común de Angular (ngIf, ngFor, etc.)
import { FormsModule } from '@angular/forms';      // Módulo para formularios template-driven
import { AvailabilityService, DoctorDTO } from '../../../core/services/availability/service'; // Servicio de disponibilidad y DTO de médicos
import { AppointmentService } from '../../../core/services/appointment/service'; // Servicio de citas
import { UserService } from '../../../core/services/user/service'; // Servicio de usuarios

@Component({
  selector: 'app-book-appointment', // Nombre del selector para usar el componente en HTML
  standalone: true,                 // Componente independiente (no necesita declararse en un módulo)
  imports: [CommonModule, FormsModule], // Módulos que el componente utiliza
  templateUrl: './component.html'   // Plantilla HTML asociada
})
export class BookAppointmentComponent implements OnInit {
  step: number = 1;              // Paso actual del flujo (1: registro paciente, 2: selección cita, etc.)
  doctors: DoctorDTO[] = [];     // Lista de médicos obtenida del backend
  availableSlots: string[] = []; // Lista de franjas horarias disponibles
  loading: boolean = false;      // Estado de carga para mostrar spinner o deshabilitar botones
  success: boolean = false;      // Estado de éxito al agendar cita
  errorMessage: string = '';     // Mensaje de error en caso de fallo
  patientId: number | null = null; // ID del paciente registrado

  // Datos del paciente a registrar
  patient = {
    documentNumber: '', firstName: '', lastName: '',
    phone: '', gender: '', birthDate: '', email: ''
  };

  // Datos de la cita a agendar
  booking = {
    doctorId: null as number | null, // ID del médico seleccionado
    date: '',                        // Fecha de la cita
    time: ''                         // Hora de la cita
  };

  // Inyección de servicios y ChangeDetectorRef
  constructor(
    private availabilityService: AvailabilityService,
    private appointmentService: AppointmentService,
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) {}

  // Método que se ejecuta al inicializar el componente
  ngOnInit() {
    // Obtiene la lista de médicos desde el backend
    this.availabilityService.getDoctors().subscribe(doctors => {
      this.doctors = doctors;
      this.cdr.detectChanges(); // Fuerza actualización de la vista
    });
  }

  // Método para registrar al paciente desde el formulario
  registerPatient() {
    this.loading = true;
    this.errorMessage = '';
    this.userService.registerFromWhatsApp(this.patient).subscribe({
      next: (response) => {
        this.patientId = response.patientId; // Guarda el ID del paciente
        this.step = 2;                       // Avanza al paso 2 (selección de cita)
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Error al registrar el paciente.'; // Mensaje de error
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Método que se ejecuta cuando cambia el médico o la fecha
  onDoctorOrDateChange() {
    if (this.booking.doctorId && this.booking.date) {
      this.availabilityService.getAvailableSlots(
        this.booking.doctorId,
        this.booking.date
      ).subscribe(slots => {
        this.availableSlots = slots; // Actualiza las franjas disponibles
        this.booking.time = '';      // Reinicia la hora seleccionada
        this.cdr.detectChanges();
      });
    }
  }

  // Método para confirmar la cita
  confirmBooking() {
    this.loading = true;
    this.errorMessage = '';
    this.appointmentService.createWebBooking({
      patientId: this.patientId!,          // ID del paciente registrado
      doctorId: this.booking.doctorId!,    // ID del médico seleccionado
      appointmentDate: this.booking.date,  // Fecha de la cita
      appointmentTime: this.booking.time,  // Hora de la cita
      notes: 'Cita agendada desde portal web' // Nota adicional
    }).subscribe({
      next: () => {
        this.success = true;   // Marca éxito
        this.loading = false;  // Desactiva estado de carga
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = err.error?.error || 'Error al agendar la cita.'; // Mensaje de error
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Método para reiniciar el flujo de agendamiento
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

  // Método para obtener el nombre y especialidad del médico seleccionado
  getDoctorName(): string {
    const doctor = this.doctors.find(d => d.id === this.booking.doctorId);
    return doctor ? `${doctor.fullName} - ${doctor.specialty}` : '';
  }
}
