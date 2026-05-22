package com.groupsoft.piedrazul.user.domain.Repository;
import com.groupsoft.piedrazul.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByDocumentNumber(String documentNumber);
}