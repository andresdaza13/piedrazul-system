package com.groupsoft.piedrazul.booking.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.List;

// @Configuration indica que esta clase define beans de configuración para Spring.
// Aquí se configura el filtro CORS que controla qué orígenes pueden consumir la API REST.
@Configuration
public class CorsConfig {

    // @Bean expone un CorsFilter como componente dentro del contexto de Spring.
    // Este filtro intercepta las solicitudes HTTP y aplica las reglas de CORS definidas.
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Define los orígenes permitidos. En este caso, solo el frontend Angular
        // que corre en http://localhost:4200 puede consumir la API.
        config.setAllowedOrigins(List.of("http://localhost:4200"));

        // Define los métodos HTTP permitidos para las solicitudes cross-origin.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Define los encabezados permitidos. Con "*" se aceptan todos.
        config.setAllowedHeaders(List.of("*"));

        // Permite el envío de credenciales (cookies, tokens de sesión) en las solicitudes.
        config.setAllowCredentials(true);

        // Asocia la configuración CORS a todas las rutas de la aplicación (/**).
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // Retorna el filtro configurado, que será aplicado globalmente.
        return new CorsFilter(source);
    }
}
