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

    public static final String EXCHANGE_NAME = "piedrazul.exchange";
    public static final String WHATSAPP_QUEUE = "whatsapp.notification.queue";
    public static final String ROUTING_KEY = "appointment.created";

    // Cola del user-service que este servicio escucha
    public static final String WHATSAPP_APPOINTMENT_QUEUE = "whatsapp.appointment.queue";

    @Bean
    public Queue whatsappQueue() {
        return new Queue(WHATSAPP_QUEUE, true);
    }

    // Declaramos la cola del user-service para que RabbitMQ la cree si no existe
    @Bean
    public Queue whatsappAppointmentQueue() {
        return new Queue(WHATSAPP_APPOINTMENT_QUEUE, true);
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
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}