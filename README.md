# Piedrazul System

Sistema de reserva de citas medicas para Piedrazul — microservicios Spring Boot + Angular.

Documentacion de arquitectura para sustentacion: [docs/ARQUITECTURA.md](docs/ARQUITECTURA.md)

## Microservicios

| Servicio | Puerto | Responsabilidad |
|----------|--------|-----------------|
| `user-service` | 8081 | Pacientes, registro web/WhatsApp |
| `availability-service` | 8082 | Medicos, franjas, ventana de agendamiento |
| `booking-service` | 8083 | Citas, CSV, re-agendamiento |

## Requisitos funcionales (3er corte)

1. Listar citas por medico/fecha — `/agendador`
2. Crear cita WhatsApp — `/agendador/nueva-cita`
3. Agendar cita web — `/paciente`
4. Configuracion admin — `/admin/disponibilidad`
5. Exportar CSV — boton en listado agendador
6. Re-agendamiento con historial — modal en listado

## Patrones GoF (8)

| Patron | Ubicacion principal |
|--------|---------------------|
| Strategy | `SlotCalculationStrategy` |
| Template Method | `AbstractSlotCalculationStrategy` |
| Facade | `WebBookingFacade` |
| Adapter | `*ClientAdapter` |
| Factory Method | `User.createPatientFromWhatsApp` |
| Observer | RabbitMQ listeners |
| Command | `RescheduleAppointmentCommand` + `CommandInvoker` |
| State | `AppointmentStateContext` |

## Ejecucion

```bash
docker-compose up -d
mvn clean install
# Terminales separadas:
cd user-service && mvn spring-boot:run
cd availability-service && mvn spring-boot:run
cd booking-service && mvn spring-boot:run
cd frontend && npm install && npm start
```

Portal: http://localhost:4200
