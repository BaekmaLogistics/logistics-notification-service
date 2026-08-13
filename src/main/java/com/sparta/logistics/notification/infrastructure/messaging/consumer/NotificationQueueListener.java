package com.sparta.logistics.notification.infrastructure.messaging.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.logistics.notification.application.command.usecase.SendOrderNotificationUseCase;
import com.sparta.logistics.notification.application.command.usecase.TransmitSlackMessageUseCase;
import com.sparta.logistics.notification.infrastructure.messaging.constant.EventType;
import com.sparta.logistics.notification.infrastructure.messaging.envelope.EventEnvelope;
import com.sparta.logistics.notification.infrastructure.messaging.event.OrderCreatedPayload;
import com.sparta.logistics.notification.infrastructure.messaging.event.TransmitSlackMessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationQueueListener {
    private final SendOrderNotificationUseCase sendOrderNotificationUseCase;
    private final TransmitSlackMessageUseCase transmitSlackMessageUseCase;
    private final ObjectMapper objectMapper;
    @RabbitListener(queues = "${message.queue.notification}")
    public void consume(Message message) {
        try {
            // 원시 byte[] 바이트를 directly EventEnvelope 객체로 파싱 (InaccessibleObjectException 해결)
            EventEnvelope<Object> event = objectMapper.readValue(
                    message.getBody(),
                    new TypeReference<EventEnvelope<Object>>() {}
            );

            EventType eventType = EventType.fromKeyString(event.header().eventType());

            if (eventType == EventType.UNDEFINED) {
                log.error("event consume Error {}", event);
                throw new IllegalArgumentException("Unsupported event type: " + event.header().eventType());
            }

            Object payload = convert(event.payload(), eventType.getPayloadClass());

            switch (eventType) {
                case ORDER_CREATED ->
                        sendOrderNotificationUseCase.send(((OrderCreatedPayload) payload).toCommand());
                case TRANSMIT_SLACK_MESSAGE ->
                        transmitSlackMessageUseCase.transmit(((TransmitSlackMessagePayload) payload).toCommand(event.header().actorId()));
            }
        } catch (IOException e) {
            log.error("Failed to deserialize RabbitMQ message body", e);
            throw new IllegalArgumentException("Invalid message payload", e);
        }
    }

    private <T> T convert(Object payload, Class<T> clazz) {
        return objectMapper.convertValue(payload, clazz);
    }
}
