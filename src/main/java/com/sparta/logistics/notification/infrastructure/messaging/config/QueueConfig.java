package com.sparta.logistics.notification.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
    @Value("${message.exchange}")
    private String exchange;

    @Value("${message.queue.notification}")
    private String queueNotification;

    @Bean
    public TopicExchange exchange() { return new TopicExchange(exchange); }

    @Bean public Queue queueNotification() { return new Queue(queueNotification); }

    @Bean public Binding bindingSlackMessageTransmit() { return BindingBuilder.bind(queueNotification()).to(exchange()).with("notification.slack-message.transmit"); }
    @Bean public Binding bindingOrderCreated() { return BindingBuilder.bind(queueNotification()).to(exchange()).with("order.created"); }
}
