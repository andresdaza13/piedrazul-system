import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DoctorDTO {
  id: number;
  fullName: string;
  specialty: string;
  active: boolean;
}

export interface AvailabilityConfigDTO {
  doctorId: number;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  intervalMinutes: number;
}

@Injectable({
  providedIn: 'root'
})
export class AvailabilityService {
  private baseUrl = 'http://localhost:8082/api/v1';

  constructor(private http: HttpClient) {}

  getDoctors(): Observable<DoctorDTO[]> {
    return this.http.get<DoctorDTO[]>(`${this.baseUrl}/doctors`);
  }

  createDoctor(doctor: {fullName: string, specialty: string}): Observable<DoctorDTO> {
    return this.http.post<DoctorDTO>(`${this.baseUrl}/doctors`, doctor);
  }

  configureAvailability(config: AvailabilityConfigDTO): Observable<any> {
    return this.http.post(`${this.baseUrl}/availability/configure`, config);
  }

  getAvailableSlots(doctorId: number, date: string): Observable<string[]> {
    return this.http.get<string[]>(
      `${this.baseUrl}/availability/slots?doctorId=${doctorId}&targetDate=${date}`
    );
  }
}