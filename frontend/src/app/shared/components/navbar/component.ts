import { Component } from '@angular/core'; // Decorador para definir un componente Angular
import { RouterLink, RouterLinkActive } from '@angular/router'; // Directivas para navegación y estado activo en rutas

@Component({
  selector: 'app-navbar', // Nombre del selector para usar el componente en HTML
  standalone: true,       // Componente independiente (no necesita declararse en un módulo)
  imports: [RouterLink, RouterLinkActive], // Módulos que el componente utiliza para navegación
  templateUrl: './component.html' // Plantilla HTML asociada
})
export class NavbarComponent {}
