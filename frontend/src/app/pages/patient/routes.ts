import { Routes } from '@angular/router'; // Importa el tipo Routes para definir rutas en Angular

// Definición de las rutas del módulo de pacientes
export const PATIENT_ROUTES: Routes = [
  {
    path: '', // Ruta raíz del módulo patient
    loadComponent: () =>
      import('./book-appointment/component').then(m => m.BookAppointmentComponent) 
      // Carga dinámica del componente BookAppointmentComponent (flujo de agendamiento de citas)
  }
];
