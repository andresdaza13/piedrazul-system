import { Injectable } from '@angular/core'; // Permite marcar la clase como inyectable en Angular
import { HttpClient } from '@angular/common/http'; // Cliente HTTP para consumir APIs REST
import { Observable } from 'rxjs'; // Tipo Observable para manejar respuestas asíncronas

// DTO de entrada para crear citas desde WhatsApp
export interface AppointmentRequestDTO {
  patientId: number;        // ID del paciente
  doctorId: number;         // ID del médico
  appointmentDate: string;  // Fecha de la cita (ISO string)
  whatsappNumber: string;   // Número de WhatsApp del paciente
  notes?: string;           // Notas opcionales
}

// DTO de entrada para crear citas desde la Web
export interface WebBookingRequestDTO {
  patientId: number;        // ID del paciente
  doctorId: number;         // ID del médico
  appointmentDate: string;  // Fecha de la cita
  appointmentTime: string;  // Hora de la cita
  notes?: string;           // Notas opcionales
}

// DTO de salida para representar una cita creada
export interface AppointmentResponseDTO {
  id: number;               // ID de la cita
  patientId: number;        // ID del paciente
  doctorId: number;         // ID del médico
  appointmentDate: string;  // Fecha de la cita
  status: string;           // Estado de la cita (PENDING, CONFIRMED, etc.)
  whatsappNumber: string;   // Número de WhatsApp asociado
  notes: string;            // Notas de la cita
}

// Servicio Angular para consumir el microservicio de citas
@Injectable({
  providedIn: 'root' // Hace que el servicio esté disponible en toda la aplicación
})
export class AppointmentService {
  private baseUrl = 'http://localhost:8083/api/v1'; // URL base del backend (Booking Service)

  constructor(private http: HttpClient) {} // Inyección del cliente HTTP

  // Método para crear una cita desde WhatsApp
  createAppointment(request: AppointmentRequestDTO): Observable<AppointmentResponseDTO> {
    return this.http.post<AppointmentResponseDTO>(
      `${this.baseUrl}/appointments`, request
    );
  }

  // Método para crear una cita desde la Web (usando el patrón Facade en backend)
  createWebBooking(request: WebBookingRequestDTO): Observable<any> {
    return this.http.post(`${this.baseUrl}/appointments/web-booking`, request);
  }

  // Método para listar citas de un médico en una fecha específica
  getAppointmentsByDoctorAndDate(doctorId: number, date: string): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/appointments/doctor/${doctorId}?date=${date}`
    );
  }
}
