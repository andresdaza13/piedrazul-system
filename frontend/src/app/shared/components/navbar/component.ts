import { Component, OnDestroy, OnInit } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { filter, Subscription } from 'rxjs';
import { AuthService, AuthSession } from '../../../core/services/auth/service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './component.html'
})
export class NavbarComponent implements OnInit, OnDestroy {
  session: AuthSession | null = null;
  private navSub?: Subscription;

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.refreshSession();
    this.navSub = this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => this.refreshSession());
  }

  ngOnDestroy(): void {
    this.navSub?.unsubscribe();
  }

  get showNavbar(): boolean {
    return this.session !== null && !this.router.url.startsWith('/login');
  }

  refreshSession(): void {
    this.session = this.auth.getSession();
  }

  logout(): void {
    this.auth.logout();
    this.session = null;
    void this.router.navigate(['/login']);
  }

  roleLabel(role: string): string {
    const labels: Record<string, string> = {
      ADMINISTRATOR: 'Administrador',
      SCHEDULER: 'Agendador',
      DOCTOR: 'Medico',
      PATIENT: 'Paciente'
    };
    return labels[role] ?? role;
  }
}
