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

    @Value("${message.queue.delivery}")
    private String queueDelivery;
    @Value("${message.queue.hub}")
    private String queueHub;
    @Value("${message.queue.notification}")
    private String queueNotification;
    @Value("${message.queue.order}")
    private String queueOrder;

    @Bean
    public TopicExchange exchange() { return new TopicExchange(exchange); }

    @Bean public Queue queueDelivery() { return new Queue(queueDelivery); }
    @Bean public Queue queueHub() { return new Queue(queueHub); }
    @Bean public Queue queueNotification() { return new Queue(queueNotification); }
    @Bean public Queue queueOrder() { return new Queue(queueOrder); }

    @Bean public Binding bindingDelivery() { return BindingBuilder.bind(queueDelivery()).to(exchange()).with(queueDelivery); }
    @Bean public Binding bindingHub() { return BindingBuilder.bind(queueHub()).to(exchange()).with(queueHub); }
    @Bean public Binding bindingNotification() { return BindingBuilder.bind(queueNotification()).to(exchange()).with(queueNotification); }
    @Bean public Binding bindingOrder() { return BindingBuilder.bind(queueNotification()).to(exchange()).with(queueOrder); }
}
