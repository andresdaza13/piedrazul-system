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
 * Adapta la comunicación de red (HTTP/REST) hacia el microservicio availability-service,
 * transformando la respuesta JSON en una lista de objetos Java utilizables por nuestro dominio.
 */
@Component
public class AvailabilityClientAdapter {

    private final RestTemplate restTemplate;
    // Asumimos que el availability-service corre en el puerto 8082
    private final String availabilityServiceUrl = "http://localhost:8082/api/v1/availability/slots";

    public AvailabilityClientAdapter() {
        this.restTemplate = new RestTemplate();
    }

    public List<LocalTime> getAvailableSlots(Long doctorId, LocalDate date) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(availabilityServiceUrl)
                    .queryParam("doctorId", doctorId)
                    .queryParam("targetDate", date.toString())
                    .toUriString();

            LocalTime[] slots = restTemplate.getForObject(url, LocalTime[].class);
            return slots != null ? Arrays.asList(slots) : Collections.emptyList();
            
        } catch (Exception e) {
            // Si el otro microservicio está caído, el adaptador maneja el fallo gracefully
            System.err.println("Error conectando con Availability Service: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}