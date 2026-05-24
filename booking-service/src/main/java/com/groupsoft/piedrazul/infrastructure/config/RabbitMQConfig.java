package com.groupsoft.piedrazul.booking.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nombres clave para nuestra arquitectura
    public static final String EXCHANGE_NAME = "piedrazul.exchange";
    public static final String WHATSAPP_QUEUE = "whatsapp.notification.queue";
    public static final String ROUTING_KEY = "appointment.created";

    @Bean
    public Queue whatsappQueue() {
        // true = durable (sobrevive a reinicios de RabbitMQ)
        return new Queue(WHATSAPP_QUEUE, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue whatsappQueue, TopicExchange exchange) {
        return BindingBuilder.bind(whatsappQueue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter(); // Transforma los objetos a JSON
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}