package com.groupsoft.piedrazul.booking.infrastructure.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class UserClientAdapter {

    private final RestTemplate restTemplate;
    private final String userServiceBaseUrl = "http://localhost:8081/api/v1/users";

    public UserClientAdapter() {
        this.restTemplate = new RestTemplate();
    }

    @SuppressWarnings("unchecked")
    public Optional<Map<String, Object>> getUserById(Long userId) {
        try {
            Map<String, Object> response = restTemplate.getForObject(
                    userServiceBaseUrl + "/" + userId, Map.class);
            if (response == null) {
                return Optional.empty();
            }
            Map<String, Object> user = new HashMap<>();
            user.put("fullName", response.get("fullName"));
            user.put("documentNumber", response.get("documentNumber"));
            user.put("phone", response.get("phone"));
            return Optional.of(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
