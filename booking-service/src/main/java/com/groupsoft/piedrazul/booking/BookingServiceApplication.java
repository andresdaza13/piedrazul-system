package com.groupsoft.piedrazul.booking;

// Importa las clases necesarias de Spring Boot
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication indica que esta clase es el punto de inicio del microservicio.
// Combina tres anotaciones: @Configuration, @EnableAutoConfiguration y @ComponentScan.
// Esto permite que Spring configure automáticamente el contexto de la aplicación.
@SpringBootApplication
public class BookingServiceApplication {

    // Método main: punto de entrada de la aplicación.
    // SpringApplication.run() arranca el microservicio, inicializa el contexto de Spring
    // y levanta el servidor embebido (por defecto Tomcat).
    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}
