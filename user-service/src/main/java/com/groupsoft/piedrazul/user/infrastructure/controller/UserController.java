package com.groupsoft.piedrazul.user.infrastructure.controller;
// Paquete de controladores de infraestructura para el servicio de usuarios

import com.groupsoft.piedrazul.user.application.dto.UserWhatsAppDTO; 
// DTO que representa los datos de usuario recibidos desde WhatsApp

import com.groupsoft.piedrazul.user.application.service.UserService; 
// Servicio de aplicación que contiene la lógica de negocio para registrar o recuperar usuarios

import com.groupsoft.piedrazul.user.domain.model.User; 
// Entidad del dominio que representa un usuario

import lombok.RequiredArgsConstructor; 
// Anotación de Lombok que genera constructor con parámetros obligatorios (final)

import org.springframework.http.HttpStatus; 
// Clase que define códigos de estado HTTP

import org.springframework.http.ResponseEntity; 
// Clase que representa una respuesta HTTP completa (estado + cuerpo)

import org.springframework.web.bind.annotation.*; 
// Anotaciones de Spring MVC para definir controladores y endpoints REST

import java.util.Map; 
// Clase estándar de Java para manejar mapas clave-valor

/**
 * Controlador REST para la gestión de usuarios.
 * Expone endpoints HTTP que permiten registrar pacientes desde WhatsApp.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service; 
    // Inyección del servicio de usuarios

    @PostMapping("/whatsapp-contact")
    public ResponseEntity<Map<String, Object>> registerFromWhatsApp(@RequestBody UserWhatsAppDTO request) {
        // Se recibe un DTO con los datos del paciente desde WhatsApp
        User user = service.registerOrGetUserFromWhatsApp(request);
        
        // Se retorna una respuesta HTTP con estado 200 OK y un cuerpo JSON
        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of(
                        "message", "Paciente procesado exitosamente", // Mensaje de confirmación
                        "patientId", user.getId()                     // ID del paciente registrado o recuperado
                )
        );
    }
}
