import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth/service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './component.html'
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';
  loading = false;

  readonly demoUsers = [
    { label: 'Administrador', user: 'admin', pass: 'admin123' },
    { label: 'Agendador', user: 'agendador', pass: 'agendador123' },
    { label: 'Medico', user: 'medico', pass: 'medico123' },
    { label: 'Paciente', user: 'paciente', pass: 'paciente123' }
  ];

  constructor(
    private auth: AuthService,
    private router: Router
  ) {
    const session = this.auth.getSession();
    if (session) {
      void this.router.navigateByUrl(session.homeRoute);
    }
  }

  fillDemo(user: string, pass: string): void {
    this.username = user;
    this.password = pass;
  }

  submit(): void {
    this.error = '';
    this.loading = true;

    this.auth.login({ username: this.username, password: this.password }).subscribe({
      next: (session) => {
        this.loading = false;
        void this.router.navigateByUrl(session.homeRoute);
      },
      error: (err) => {
        this.loading = false;
        this.error =
          err?.error?.message ?? 'No fue posible iniciar sesion. Verifique sus credenciales.';
      }
    });
  }
}
