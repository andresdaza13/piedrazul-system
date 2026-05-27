import { Routes } from '@angular/router';

export const PATIENT_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./book-appointment/component').then(m => m.BookAppointmentComponent)
  }
];