package com.sparta.logistics.notification.infrastructure.messaging.producer;

import com.sparta.logistics.notification.application.command.dto.SendSlackMessageCommand;
import com.sparta.logistics.notification.application.command.producer.TransmitSlackMessageEventProducer;
import com.sparta.logistics.notification.infrastructure.messaging.event.ProcessSlackMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RabbitMQTransmitSlackMessageEventProducer implements TransmitSlackMessageEventProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${message.exchange}")
    private String exchange;

    @Value("${message.queue.notification}")
    private String queueNotification;

    @Override
    public void produce(SendSlackMessageCommand command, UUID userId) {
        ProcessSlackMessageEvent event = ProcessSlackMessageEvent.from(command, userId);

        rabbitTemplate.convertAndSend(exchange, queueNotification, event);
    }
}
