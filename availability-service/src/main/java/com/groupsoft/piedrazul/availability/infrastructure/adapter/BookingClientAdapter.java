package com.groupsoft.piedrazul.availability.infrastructure.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class BookingClientAdapter {

    private final RestTemplate restTemplate;
    private final String occupiedSlotsUrl =
            "http://localhost:8083/api/v1/appointments/doctor";

    public BookingClientAdapter() {
        this.restTemplate = new RestTemplate();
    }

    public List<LocalTime> getOccupiedSlots(Long doctorId, LocalDate date) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(occupiedSlotsUrl + "/" + doctorId + "/occupied-times")
                    .queryParam("date", date.toString())
                    .toUriString();

            LocalTime[] occupied = restTemplate.getForObject(url, LocalTime[].class);
            return occupied != null ? Arrays.asList(occupied) : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
