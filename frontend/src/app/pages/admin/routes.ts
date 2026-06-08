import { Routes } from '@angular/router'; // Importa el tipo Routes para definir rutas en Angular

// Definición de las rutas del módulo de administración
export const ADMIN_ROUTES: Routes = [
  {
    path: '', // Ruta raíz del módulo admin
    loadComponent: () =>
      import('./manage-doctors/component').then(m => m.ManageDoctorsComponent) 
      // Carga dinámica del componente ManageDoctorsComponent (gestión de médicos)
  },
  {
    path: 'disponibilidad', // Ruta para configurar disponibilidad de médicos
    loadComponent: () =>
      import('./configure-availability/component').then(m => m.ConfigureAvailabilityComponent) 
      // Carga dinámica del componente ConfigureAvailabilityComponent (configuración de horarios)
  }
];
