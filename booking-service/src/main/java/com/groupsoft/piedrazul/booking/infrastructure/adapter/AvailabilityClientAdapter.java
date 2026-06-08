package com.groupsoft.piedrazul.booking.infrastructure.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

/**
 * PATRÓN ADAPTER (Estructural): 
 * Este componente actúa como un puente entre nuestro dominio y el microservicio externo availability-service.
 * Su responsabilidad es transformar la respuesta JSON (slots disponibles) en objetos Java (LocalTime) 
 * que puedan ser usados directamente en la lógica de negocio del booking-service.
 */
@Component
public class AvailabilityClientAdapter {

    // Cliente HTTP que permite realizar solicitudes REST a otros servicios.
    private final RestTemplate restTemplate;

    // URL base del microservicio availability-service.
    // Aquí se asume que corre en localhost:8082 y expone el endpoint /api/v1/availability/slots.
    private final String availabilityServiceUrl = "http://localhost:8082/api/v1/availability/slots";

    // Constructor: inicializa el RestTemplate.
    // RestTemplate es un cliente síncrono para consumir APIs REST.
    public AvailabilityClientAdapter() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Método principal del adaptador:
     * Consulta los slots disponibles para un médico en una fecha específica.
     *
     * @param doctorId Identificador único del médico/terapista.
     * @param date Fecha en la que se desea consultar disponibilidad.
     * @return Lista de horas disponibles (LocalTime) o lista vacía si no hay disponibilidad.
     */
    public List<LocalTime> getAvailableSlots(Long doctorId, LocalDate date) {
        try {
            // Construcción dinámica de la URL con parámetros doctorId y targetDate.
            String url = UriComponentsBuilder.fromHttpUrl(availabilityServiceUrl)
                    .queryParam("doctorId", doctorId)
                    .queryParam("targetDate", date.toString())
                    .toUriString();

            // Llamada HTTP GET al microservicio availability-service.
            // Se espera un arreglo de LocalTime como respuesta.
            LocalTime[] slots = restTemplate.getForObject(url, LocalTime[].class);

            // Si la respuesta no es nula, se convierte el arreglo en lista.
            // En caso contrario, se retorna una lista vacía.
            return slots != null ? Arrays.asList(slots) : Collections.emptyList();
            
        } catch (Exception e) {
            // Manejo de errores: si el availability-service está caído o hay problemas de red,
            // se captura la excepción y se retorna una lista vacía para evitar que el sistema falle.
            System.err.println("Error conectando con Availability Service: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
