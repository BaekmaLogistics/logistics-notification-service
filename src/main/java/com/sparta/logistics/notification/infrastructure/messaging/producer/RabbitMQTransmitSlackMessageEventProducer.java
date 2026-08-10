package com.sparta.logistics.notification.infrastructure.messaging.producer;

import com.sparta.logistics.notification.application.command.producer.TransmitSlackMessageEventProducer;
import com.sparta.logistics.notification.infrastructure.messaging.envelope.EventEnvelope;
import com.sparta.logistics.notification.infrastructure.messaging.event.TransmitSlackMessagePayload;
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

    @Override
    public void produce(UUID slackMessageId, UUID actorId) {
        TransmitSlackMessagePayload payload = new TransmitSlackMessagePayload(slackMessageId);

        EventEnvelope<TransmitSlackMessagePayload> event = EventEnvelope.of(
                "TransmitSlackMessage",
                payload,
                actorId
        );

        rabbitTemplate.convertAndSend(exchange, EventRoutingKey.TRANSMIT_SLACK_MESSAGE.getKey(), event);
    }
}
