package com.groupsoft.piedrazul.user.infrastructure.config;
// Paquete de configuración de infraestructura para el servicio de usuarios

import org.springframework.amqp.core.Queue; 
// Clase que representa una cola en RabbitMQ

import org.springframework.amqp.rabbit.connection.ConnectionFactory; 
// Fábrica de conexiones para RabbitMQ

import org.springframework.amqp.rabbit.core.RabbitTemplate; 
// Componente de Spring AMQP para enviar mensajes a RabbitMQ

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter; 
// Convertidor que serializa objetos Java a JSON para enviarlos a RabbitMQ

import org.springframework.amqp.support.converter.MessageConverter; 
// Interfaz para definir convertidores de mensajes en RabbitMQ

import org.springframework.context.annotation.Bean; 
// Anotación para declarar un método como productor de un bean administrado por Spring

import org.springframework.context.annotation.Configuration; 
// Anotación que marca la clase como fuente de configuración de Spring

/**
 * Configuración de RabbitMQ para el servicio de usuarios.
 * Define la cola utilizada para recibir solicitudes de citas desde WhatsApp
 * y configura el convertidor de mensajes JSON.
 */
@Configuration
public class RabbitMQConfig {

    // Nombre de la cola donde se publican los eventos de citas desde WhatsApp
    public static final String WHATSAPP_QUEUE = "whatsapp.appointment.queue";

    @Bean
    public Queue whatsappQueue() {
        // Declaración de la cola persistente (durable = true)
        return new Queue(WHATSAPP_QUEUE, true);
    }

    // Convertidor JSON para serializar objetos a RabbitMQ
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        // Configuración del RabbitTemplate con el convertidor JSON
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
