package com.groupsoft.piedrazul.booking.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration indica que esta clase define beans de configuración para Spring.
// Aquí se configuran colas, exchange y bindings de RabbitMQ.
@Configuration
public class RabbitMQConfig {

    // Nombre del exchange principal del sistema Piedrazul.
    public static final String EXCHANGE_NAME = "piedrazul.exchange";

    // Cola para notificaciones de WhatsApp (ej. confirmación de citas).
    public static final String WHATSAPP_QUEUE = "whatsapp.notification.queue";

    // Routing key usada para enrutar mensajes de creación de citas.
    public static final String ROUTING_KEY = "appointment.created";

    // Cola del user-service que este servicio escucha (para solicitudes de citas vía WhatsApp).
    public static final String WHATSAPP_APPOINTMENT_QUEUE = "whatsapp.appointment.queue";

    // Declaración de la cola de notificaciones WhatsApp.
    // El segundo parámetro "true" indica que la cola es duradera (persiste aunque RabbitMQ se reinicie).
    @Bean
    public Queue whatsappQueue() {
        return new Queue(WHATSAPP_QUEUE, true);
    }

    // Declaración de la cola del user-service.
    // Se asegura que RabbitMQ cree la cola si no existe.
    @Bean
    public Queue whatsappAppointmentQueue() {
        return new Queue(WHATSAPP_APPOINTMENT_QUEUE, true);
    }

    // Declaración del exchange de tipo Topic.
    // Permite enrutar mensajes basados en patrones de routing key.
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // Binding: conecta la cola whatsappQueue con el exchange usando la routing key "appointment.created".
    // Esto asegura que los mensajes de creación de citas lleguen a la cola correcta.
    @Bean
    public Binding binding(Queue whatsappQueue, TopicExchange exchange) {
        return BindingBuilder.bind(whatsappQueue).to(exchange).with(ROUTING_KEY);
    }

    // Conversor de mensajes: transforma objetos Java en JSON y viceversa.
    // Esto facilita enviar/recibir mensajes estructurados entre microservicios.
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Plantilla AMQP: facilita enviar mensajes a RabbitMQ.
    // Se configura con el conversor JSON para serializar automáticamente los objetos.
    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
