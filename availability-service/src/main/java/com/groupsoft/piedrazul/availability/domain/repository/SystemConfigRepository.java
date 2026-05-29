package com.groupsoft.piedrazul.availability.domain.repository;

import com.groupsoft.piedrazul.availability.domain.model.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
}
