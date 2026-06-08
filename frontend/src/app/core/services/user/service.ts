import { Injectable } from '@angular/core'; // Permite marcar la clase como inyectable en Angular
import { HttpClient } from '@angular/common/http'; // Cliente HTTP para consumir APIs REST
import { Observable } from 'rxjs'; // Tipo Observable para manejar respuestas asíncronas

// DTO para representar un usuario registrado desde WhatsApp
export interface UserWhatsAppDTO {
  documentNumber: string; // Número de documento del paciente
  firstName: string;      // Nombre del paciente
  lastName: string;       // Apellido del paciente
  phone: string;          // Teléfono del paciente
  gender: string;         // Género del paciente
  birthDate?: string;     // Fecha de nacimiento (opcional)
  email?: string;         // Correo electrónico (opcional)
}

// Servicio Angular para consumir el microservicio de usuarios
@Injectable({
  providedIn: 'root' // Hace que el servicio esté disponible en toda la aplicación
})
export class UserService {
  private baseUrl = 'http://localhost:8081/api/v1'; // URL base del backend (User Service)

  constructor(private http: HttpClient) {} // Inyección del cliente HTTP

  // Método para registrar un usuario desde WhatsApp
  registerFromWhatsApp(user: UserWhatsAppDTO): Observable<any> {
    return this.http.post(`${this.baseUrl}/users/whatsapp-contact`, user);
  }
}
