import { Routes } from '@angular/router';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./manage-doctors/component').then(m => m.ManageDoctorsComponent)
  },
  {
    path: 'disponibilidad',
    loadComponent: () =>
      import('./configure-availability/component').then(m => m.ConfigureAvailabilityComponent)
  }
];