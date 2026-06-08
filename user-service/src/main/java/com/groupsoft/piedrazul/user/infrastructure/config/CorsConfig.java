package com.groupsoft.piedrazul.user.infrastructure.config;
// Paquete de configuración de infraestructura para el servicio de usuarios

import org.springframework.context.annotation.Bean; 
// Anotación para declarar un método como productor de un bean administrado por Spring

import org.springframework.context.annotation.Configuration; 
// Anotación que marca la clase como fuente de configuración de Spring

import org.springframework.web.cors.CorsConfiguration; 
// Clase que define las reglas de CORS (Cross-Origin Resource Sharing)

import org.springframework.web.cors.UrlBasedCorsConfigurationSource; 
// Fuente de configuración basada en URL para aplicar reglas de CORS

import org.springframework.web.filter.CorsFilter; 
// Filtro que intercepta solicitudes HTTP y aplica las reglas de CORS

import java.util.List; 
// Clase estándar de Java para manejar listas

/**
 * Configuración global de CORS para el servicio de usuarios.
 * Permite que el frontend (ej. Angular en localhost:4200) consuma los endpoints REST
 * sin bloqueos por políticas de navegador.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Orígenes permitidos: solo el frontend Angular en localhost:4200
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        
        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Encabezados permitidos (todos con "*")
        config.setAllowedHeaders(List.of("*"));
        
        // Permite credenciales (cookies, tokens de sesión)
        config.setAllowCredentials(true);

        // Aplica la configuración a todas las rutas del servicio
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        // Retorna el filtro configurado
        return new CorsFilter(source);
    }
}
