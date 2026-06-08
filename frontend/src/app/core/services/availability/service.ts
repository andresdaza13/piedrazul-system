import { Injectable } from '@angular/core'; // Permite marcar la clase como inyectable en Angular
import { HttpClient } from '@angular/common/http'; // Cliente HTTP para consumir APIs REST
import { Observable } from 'rxjs'; // Tipo Observable para manejar respuestas asíncronas

// DTO para representar un médico
export interface DoctorDTO {
  id: number;           // ID del médico
  fullName: string;     // Nombre completo
  specialty: string;    // Especialidad
  active: boolean;      // Estado activo/inactivo
}

// DTO para configurar la disponibilidad de un médico
export interface AvailabilityConfigDTO {
  doctorId: number;     // ID del médico
  dayOfWeek: string;    // Día de la semana (ej: MONDAY)
  startTime: string;    // Hora de inicio (formato HH:mm)
  endTime: string;      // Hora de fin (formato HH:mm)
  intervalMinutes: number; // Intervalo en minutos entre citas
}

// Servicio Angular para consumir el microservicio de disponibilidad
@Injectable({
  providedIn: 'root' // Hace que el servicio esté disponible en toda la aplicación
})
export class AvailabilityService {
  private baseUrl = 'http://localhost:8082/api/v1'; // URL base del backend (Availability Service)

  constructor(private http: HttpClient) {} // Inyección del cliente HTTP

  // Método para obtener todos los médicos registrados
  getDoctors(): Observable<DoctorDTO[]> {
    return this.http.get<DoctorDTO[]>(`${this.baseUrl}/doctors`);
  }

  // Método para crear un nuevo médico
  createDoctor(doctor: {fullName: string, specialty: string}): Observable<DoctorDTO> {
    return this.http.post<DoctorDTO>(`${this.baseUrl}/doctors`, doctor);
  }

  // Método para configurar la disponibilidad de un médico
  configureAvailability(config: AvailabilityConfigDTO): Observable<any> {
    return this.http.post(`${this.baseUrl}/availability/configure`, config);
  }

  // Método para obtener las franjas horarias disponibles de un médico en una fecha específica
  getAvailableSlots(doctorId: number, date: string): Observable<string[]> {
    return this.http.get<string[]>(
      `${this.baseUrl}/availability/slots?doctorId=${doctorId}&targetDate=${date}`
    );
  }
}
