package com.sparta.logistics.notification.infrastructure.messaging.consumer;

import com.sparta.logistics.notification.application.command.usecase.SendOrderNotificationUseCase;
import com.sparta.logistics.notification.infrastructure.messaging.envelope.EventEnvelope;
import com.sparta.logistics.notification.infrastructure.messaging.event.OrderCreatedPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventConsumer {
    private final SendOrderNotificationUseCase sendOrderNotificationUseCase;

    @RabbitListener(queues = "${message.queue.order}")
    public void consume(EventEnvelope<OrderCreatedPayload> event) {
        OrderCreatedPayload payload = event.payload();

        sendOrderNotificationUseCase.send(
                payload.toCommand()
        );
    }
}
