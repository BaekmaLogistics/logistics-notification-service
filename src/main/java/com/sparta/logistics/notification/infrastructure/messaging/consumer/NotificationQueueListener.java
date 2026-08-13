package com.sparta.logistics.notification.infrastructure.messaging.consumer;

import com.sparta.logistics.notification.application.command.usecase.SendOrderNotificationUseCase;
import com.sparta.logistics.notification.application.command.usecase.TransmitSlackMessageUseCase;
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

    @RabbitHandler
    public void consume(EventEnvelope<?> event) {
        Object payload = event.payload();

        if (payload instanceof OrderCreatedPayload p) {
            sendOrderNotificationUseCase.send(p.toCommand());
        } else if (payload instanceof TransmitSlackMessagePayload p) {
            transmitSlackMessageUseCase.transmit(p.toCommand(event.header().actorId()));
        } else {
            throw new IllegalArgumentException("Unsupported payload type: " + payload.getClass());
        }
    }
}
