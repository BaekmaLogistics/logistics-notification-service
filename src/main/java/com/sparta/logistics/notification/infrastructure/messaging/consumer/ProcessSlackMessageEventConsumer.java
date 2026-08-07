package com.sparta.logistics.notification.infrastructure.messaging.consumer;

import com.sparta.logistics.notification.application.command.usecase.TransmitSlackMessageUseCase;
import com.sparta.logistics.notification.infrastructure.messaging.envelope.EventEnvelope;
import com.sparta.logistics.notification.infrastructure.messaging.event.TransmitSlackMessagePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessSlackMessageEventConsumer {
    private final TransmitSlackMessageUseCase transmitSlackMessageUseCase;

    @RabbitListener(queues = "${message.queue.notification}")
    public void consume(EventEnvelope<TransmitSlackMessagePayload> event) {
        TransmitSlackMessagePayload payload = event.payload();

        transmitSlackMessageUseCase.transmit(payload.toCommand(event.header().actorId()));
    }
}
