package com.groupsoft.piedrazul.availability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication: anotación principal de Spring Boot.
// Combina tres anotaciones:
// - @Configuration → permite definir beans de configuración.
// - @EnableAutoConfiguration → habilita la configuración automática de Spring Boot.
// - @ComponentScan → escanea los paquetes para detectar componentes (@Service, @Repository, @Controller).
@SpringBootApplication
public class AvailabilityServiceApplication {

    // Método main: punto de entrada de la aplicación.
    // SpringApplication.run() arranca el contexto de Spring Boot,
    // inicializa los beans y levanta el servidor embebido (por defecto Tomcat).
    public static void main(String[] args) {
        SpringApplication.run(AvailabilityServiceApplication.class, args);
    }
}
