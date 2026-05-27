import { Routes } from '@angular/router';

export const SCHEDULER_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./appointment-list/component').then(m => m.AppointmentListComponent)
  },
  {
    path: 'nueva-cita',
    loadComponent: () =>
      import('./create-appointment/component').then(m => m.CreateAppointmentComponent)
  }
];