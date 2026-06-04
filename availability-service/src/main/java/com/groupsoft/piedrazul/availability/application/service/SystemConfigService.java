package com.groupsoft.piedrazul.availability.application.service;

import com.groupsoft.piedrazul.availability.application.dto.SystemConfigDTO;
import com.groupsoft.piedrazul.availability.domain.model.SystemConfig;
import com.groupsoft.piedrazul.availability.domain.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private static final long CONFIG_ID = 1L;
    private static final int DEFAULT_BOOKING_WINDOW_WEEKS = 4;

    private final SystemConfigRepository repository;

    @Transactional(readOnly = true)
    public SystemConfigDTO getConfig() {
        SystemConfig config = repository.findById(CONFIG_ID)
                .orElseGet(this::createDefaultConfig);
        return toDto(config);
    }

    @Transactional
    public SystemConfigDTO updateBookingWindowWeeks(int weeks) {
        if (weeks < 1 || weeks > 52) {
            throw new IllegalArgumentException(
                    "La ventana de agendamiento debe estar entre 1 y 52 semanas.");
        }
        SystemConfig config = repository.findById(CONFIG_ID)
                .orElseGet(this::createDefaultConfig);
        config.setBookingWindowWeeks(weeks);
        return toDto(repository.save(config));
    }

    @Transactional(readOnly = true)
    public int getBookingWindowWeeks() {
        return getConfig().getBookingWindowWeeks();
    }

    private SystemConfig createDefaultConfig() {
        SystemConfig config = SystemConfig.builder()
                .id(CONFIG_ID)
                .bookingWindowWeeks(DEFAULT_BOOKING_WINDOW_WEEKS)
                .build();
        return repository.save(config);
    }

    private SystemConfigDTO toDto(SystemConfig config) {
        return SystemConfigDTO.builder()
                .bookingWindowWeeks(config.getBookingWindowWeeks())
                .build();
    }
}
