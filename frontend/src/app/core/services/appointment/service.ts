import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AppointmentRequestDTO {
  patientId: number;
  doctorId: number;
  appointmentDate: string;
  whatsappNumber: string;
  notes?: string;
}

export interface WebBookingRequestDTO {
  patientId: number;
  doctorId: number;
  appointmentDate: string;
  appointmentTime: string;
  notes?: string;
}

export interface AppointmentResponseDTO {
  id: number;
  patientId: number;
  doctorId: number;
  appointmentDate: string;
  status: string;
  whatsappNumber: string;
  notes: string;
}

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private baseUrl = 'http://localhost:8083/api/v1';

  constructor(private http: HttpClient) {}

  createAppointment(request: AppointmentRequestDTO): Observable<AppointmentResponseDTO> {
    return this.http.post<AppointmentResponseDTO>(
      `${this.baseUrl}/appointments`, request
    );
  }

  createWebBooking(request: WebBookingRequestDTO): Observable<any> {
    return this.http.post(`${this.baseUrl}/appointments/web-booking`, request);
  }

  getAppointmentsByDoctorAndDate(doctorId: number, date: string): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/appointments/doctor/${doctorId}?date=${date}`
    );
  }
}