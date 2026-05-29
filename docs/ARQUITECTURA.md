# Documento de Arquitectura — Piedrazul System (3er corte)

## 1. Introduccion

Sistema de reserva de citas medicas para Piedrazul, construido con **microservicios** (Spring Boot 3.2) y portal web (Angular 21). Cada servicio posee base de datos PostgreSQL aislada y comunicacion HTTP; RabbitMQ para eventos asincronos.

## 2. Historias de usuario y criterios de aceptacion

### Iteracion 1–2 (cortes anteriores)

| ID | Historia | Criterios de aceptacion |
|----|----------|------------------------|
| HU-1 | Como agendador listo citas por medico y fecha | Tabla con citas, total visible, filtros por medico/fecha |
| HU-2 | Como agendador creo cita WhatsApp | Captura documento, nombres, apellidos, celular, genero, fecha nac. (opc.), email (opc.), medico, hora respetando intervalo |

### Iteracion 3 (tercer corte)

| ID | Historia | Criterios de aceptacion |
|----|----------|------------------------|
| HU-3 | Como paciente agendo por web | Registro de usuario, seleccion de franjas disponibles, cita confirmada sin WhatsApp |
| HU-4 | Como administrador configuro el sistema | Ventana en semanas, dias de atencion, franja horaria e intervalo por medico |
| HU-5 | Como medico/agendador exporto citas CSV | Archivo descargable por medico y fecha con datos del paciente |
| HU-6 | Como medico re-agendo citas | Nueva fecha validada, historial con fecha anterior, nueva y responsable |

## 3. Requisitos funcionales — trazabilidad

| Req | Implementacion |
|-----|----------------|
| 1 | `GET /appointments/doctor/{id}?date=` + pantalla `/agendador` |
| 2 | `POST /users/whatsapp-contact` + `POST /appointments` + `/agendador/nueva-cita` |
| 3 | `POST /users/web-register` + `POST /appointments/web-booking` + `/paciente` |
| 4 | `PUT /system-config/booking-window` + `POST /availability/configure` + `/admin/disponibilidad` |
| 5 | `GET /appointments/doctor/{id}/export?date=` + boton Exportar CSV |
| 6 | `PUT /appointments/{id}/reschedule` + historial + modal re-agendar |

## 4. Escenarios de calidad

### Modificabilidad

- **Contexto:** Nuevo algoritmo de franjas (ej. citas de 45 min solo los viernes).
- **Estimulo:** Administracion solicita cambio de reglas de calculo.
- **Respuesta:** Se agrega nueva clase `SlotCalculationStrategy` sin modificar `AvailabilityService` (Open/Closed + Strategy).
- **Medicion:** Tiempo para agregar estrategia < 1 hora sin regresiones en tests.
- **Resultado esperado:** Despliegue solo de `availability-service`.

### Escalabilidad

- **Contexto:** Aumento de pacientes agendando por web.
- **Estimulo:** 10x peticiones de franjas disponibles.
- **Respuesta:** Escalar instancias de `availability-service` y `booking-service` independientemente.
- **Medicion:** Latencia p95 de `/availability/slots` estable al duplicar instancias.
- **Resultado esperado:** Booking y users no se ven afectados.

### Seguridad

- Validacion Jakarta en DTOs (`@Valid`, `@NotBlank`, `@NotNull`).
- CORS restringido a `http://localhost:4200`.
- Contraseñas generadas para pacientes (UUID) en Factory Method.
- Mensajes de error normalizados sin exponer stack traces.

## 5. Arquitectura C4 (resumen)

### Contexto

Pacientes, agendadores y administradores usan el **Portal Angular**, que consume tres APIs REST.

### Contenedores

- **user-service (8081):** usuarios/pacientes
- **availability-service (8082):** medicos, disponibilidad, configuracion global
- **booking-service (8083):** citas, CSV, re-agendamiento
- **PostgreSQL:** `users_db`, `availability_db`, `booking_db`
- **RabbitMQ:** eventos WhatsApp y notificacion de citas creadas

### Componentes (booking-service)

`AppointmentController` → `WebBookingFacade` / `AppointmentService` / `CommandInvoker` → repositorios JPA → adapters HTTP hacia otros servicios.

## 6. Patrones GoF implementados

| Patron | Tipo | Clase(s) | Problema que resuelve |
|--------|------|----------|------------------------|
| **Strategy** | Comportamiento | `SlotCalculationStrategy`, `StandardSlotCalculationStrategy` | Variar algoritmo de franjas sin cambiar el servicio |
| **Template Method** | Comportamiento | `AbstractSlotCalculationStrategy` | Reutilizar esqueleto del calculo (validar dia → generar slots) |
| **Facade** | Estructural | `WebBookingFacade` | Simplificar agendamiento web (disponibilidad + crear cita) |
| **Adapter** | Estructural | `AvailabilityClientAdapter`, `UserClientAdapter`, `BookingClientAdapter` | Integrar APIs REST entre microservicios |
| **Factory Method** | Creacion | `User.createPatientFromWhatsApp` | Crear pacientes con reglas consistentes |
| **Observer** | Comportamiento | `UserService` + `WhatsAppAppointmentListener`, `AppointmentCreatedNotificationListener` | Desacoplar registro WhatsApp y notificar citas creadas |
| **Command** | Comportamiento | `RescheduleAppointmentCommand`, `CommandInvoker`, `RescheduleAppointmentCommandHandler` | Encapsular re-agendamiento extensible |
| **State** | Comportamiento | `AppointmentStateContext`, `PendingAppointmentState`, `TerminalAppointmentState` | Reglas segun estado de la cita (cancelada no se re-agenda) |

## 7. Principios SOLID

| Principio | Evidencia |
|-----------|-----------|
| **S** | Servicios por dominio: `DoctorService`, `AvailabilityService`, `AppointmentService` |
| **O** | Nuevas estrategias de slots sin modificar codigo existente |
| **L** | Estrategias intercambiables via `SlotCalculationStrategy` |
| **I** | Interfaces pequenas: `CommandHandler`, `SlotCalculationStrategy` |
| **D** | Servicios dependen de abstracciones inyectadas por Spring |

## 8. Patron de microservicios

- Base de datos por servicio
- Comunicacion sincrona REST (Adapter) y asincrona RabbitMQ (Observer)
- Fronteras alineadas a capacidades de negocio

## 9. Repositorio

URL: repositorio Git del equipo (completar con la URL oficial del grupo).
