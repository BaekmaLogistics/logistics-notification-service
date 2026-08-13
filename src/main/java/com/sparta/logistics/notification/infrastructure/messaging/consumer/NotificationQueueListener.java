package com.sparta.logistics.notification.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.logistics.notification.application.command.usecase.SendOrderNotificationUseCase;
import com.sparta.logistics.notification.application.command.usecase.TransmitSlackMessageUseCase;
import com.sparta.logistics.notification.infrastructure.messaging.constant.EventType;
import com.sparta.logistics.notification.infrastructure.messaging.envelope.EventEnvelope;
import com.sparta.logistics.notification.infrastructure.messaging.event.OrderCreatedPayload;
import com.sparta.logistics.notification.infrastructure.messaging.event.TransmitSlackMessagePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@RabbitListener(queues = "${message.queue.notification}")
public class NotificationQueueListener {
    private final SendOrderNotificationUseCase sendOrderNotificationUseCase;
    private final TransmitSlackMessageUseCase transmitSlackMessageUseCase;
    private final ObjectMapper objectMapper;

    @RabbitHandler
    public void consume(EventEnvelope<Object> event) {
        EventType eventType = EventType.fromKeyString(event.header().eventType());

        if (eventType == EventType.UNDEFINED) {
            throw new IllegalArgumentException("Unsupported event type: " + event.header().eventType());
        }

        // 공통 변환
        Object payload = convert(event.payload(), eventType.getPayloadClass());

        // 비즈니스 로직 디스패칭
        switch (eventType) {
            case ORDER_CREATED -> sendOrderNotificationUseCase.send(((OrderCreatedPayload) payload).toCommand());
            case TRANSMIT_SLACK_MESSAGE -> transmitSlackMessageUseCase.transmit(((TransmitSlackMessagePayload) payload).toCommand(event.header().actorId()));
        }
    }

    private <T> T convert(Object payload, Class<T> clazz) {
        return objectMapper.convertValue(payload, clazz);
    }
}
