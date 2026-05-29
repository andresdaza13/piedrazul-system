import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserWhatsAppDTO {
  documentNumber: string;
  firstName: string;
  lastName: string;
  phone: string;
  gender: string;
  birthDate?: string;
  email?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = 'http://localhost:8081/api/v1';

  constructor(private http: HttpClient) {}

  registerFromWhatsApp(user: UserWhatsAppDTO): Observable<any> {
    return this.http.post(`${this.baseUrl}/users/whatsapp-contact`, user);
  }

  registerWebPatient(user: UserWhatsAppDTO): Observable<any> {
    return this.http.post(`${this.baseUrl}/users/web-register`, user);
  }
}