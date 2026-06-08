import { Routes } from '@angular/router'; // Importa el tipo Routes para definir rutas en Angular

// Definición de las rutas del módulo scheduler (agendador/administrador)
export const SCHEDULER_ROUTES: Routes = [
  {
    path: '', // Ruta raíz del módulo scheduler
    loadComponent: () =>
      import('./appointment-list/component').then(m => m.AppointmentListComponent)
      // Carga dinámica del componente AppointmentListComponent (listado de citas por médico y fecha)
  },
  {
    path: 'nueva-cita', // Ruta para crear nuevas citas desde el portal administrativo
    loadComponent: () =>
      import('./create-appointment/component').then(m => m.CreateAppointmentComponent)
      // Carga dinámica del componente CreateAppointmentComponent (creación de citas)
  }
];
