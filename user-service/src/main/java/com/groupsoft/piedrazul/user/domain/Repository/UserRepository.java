package com.groupsoft.piedrazul.user.domain.Repository;
// Paquete de repositorios del dominio User

import com.groupsoft.piedrazul.user.domain.model.User; 
// Entidad del dominio que representa un usuario

import org.springframework.data.jpa.repository.JpaRepository; 
// Interfaz de Spring Data JPA que provee operaciones CRUD y consultas sobre la entidad

import org.springframework.stereotype.Repository; 
// Anotación que marca la interfaz como un repositorio de Spring

import java.util.Optional; 
// Clase estándar de Java para manejar valores opcionales (puede estar presente o no)

/**
 * Repositorio JPA para la entidad User.
 * Define métodos de búsqueda personalizados por username, documento y teléfono.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);          // Busca usuario por nombre de usuario
    Optional<User> findByDocumentNumber(String documentNumber); // Busca usuario por número de documento
    Optional<User> findByPhone(String phone);                // Busca usuario por número de teléfono
}
