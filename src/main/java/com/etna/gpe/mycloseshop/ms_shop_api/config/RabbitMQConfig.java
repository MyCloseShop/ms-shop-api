package com.etna.gpe.mycloseshop.ms_shop_api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.exchange.appointments}")
    private String appointmentsExchange;

    @Value("${rabbitmq.queue.appointments.created}")
    private String appointmentCreatedQueue;

    @Value("${rabbitmq.queue.appointments.paid}")
    private String appointmentPaidQueue;

    @Bean
    public TopicExchange appointmentExchange() {
        return new TopicExchange(appointmentsExchange);
    }

    @Bean
    public Queue appointmentCreatedQueue() {
        return new Queue(appointmentCreatedQueue);
    }

    @Bean
    public Queue appointmentPaidQueue() {
        return new Queue(appointmentPaidQueue);
    }

    @Bean
    public Binding bindAppointmentCreatedQueue(TopicExchange exchange, Queue appointmentCreatedQueue) {
        return BindingBuilder.bind(appointmentCreatedQueue).to(exchange).with("appointments.created");
    }

    @Bean
    public Binding bindAppointmentPaidQueue(TopicExchange exchange, Queue appointmentPaidQueue) {
        return BindingBuilder.bind(appointmentPaidQueue).to(exchange).with("appointments.paid");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
