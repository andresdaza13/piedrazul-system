package com.groupsoft.piedrazul.user;
// Paquete raíz del microservicio User

import org.springframework.boot.SpringApplication; 
// Clase principal para arrancar aplicaciones Spring Boot

import org.springframework.boot.autoconfigure.SpringBootApplication; 
// Anotación que habilita configuración automática, escaneo de componentes y define la aplicación como Spring Boot

/**
 * Clase principal del microservicio UserService.
 * Es el punto de entrada de la aplicación Spring Boot.
 * 
 * - @SpringBootApplication combina:
 *   @Configuration, @EnableAutoConfiguration y @ComponentScan.
 * - El método main arranca el contexto de Spring y levanta el servidor embebido (Tomcat por defecto).
 */
@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        // Arranca la aplicación Spring Boot, inicializa el contexto y expone los endpoints REST
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
