import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/component').then((m) => m.LoginComponent)
  },
  {
    path: 'agendador',
    canActivate: [authGuard, roleGuard(['SCHEDULER', 'DOCTOR'])],
    loadChildren: () =>
      import('./pages/scheduler/routes').then((m) => m.SCHEDULER_ROUTES)
  },
  {
    path: 'paciente',
    canActivate: [authGuard, roleGuard(['PATIENT'])],
    loadChildren: () => import('./pages/patient/routes').then((m) => m.PATIENT_ROUTES)
  },
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard(['ADMINISTRATOR'])],
    loadChildren: () => import('./pages/admin/routes').then((m) => m.ADMIN_ROUTES)
  }
];
