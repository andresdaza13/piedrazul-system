import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export type UserRole = 'ADMINISTRATOR' | 'SCHEDULER' | 'DOCTOR' | 'PATIENT';

export interface AuthSession {
  id: number;
  fullName: string;
  username: string;
  role: UserRole;
  homeRoute: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

const STORAGE_KEY = 'piedrazul.auth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = 'http://localhost:8081/api/v1/users';

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequest): Observable<AuthSession> {
    return this.http
      .post<AuthSession>(`${this.baseUrl}/login`, credentials)
      .pipe(tap((session) => sessionStorage.setItem(STORAGE_KEY, JSON.stringify(session))));
  }

  logout(): void {
    sessionStorage.removeItem(STORAGE_KEY);
  }

  getSession(): AuthSession | null {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as AuthSession;
    } catch {
      return null;
    }
  }

  isLoggedIn(): boolean {
    return this.getSession() !== null;
  }

  hasRole(...roles: UserRole[]): boolean {
    const session = this.getSession();
    return session !== null && roles.includes(session.role);
  }
}
