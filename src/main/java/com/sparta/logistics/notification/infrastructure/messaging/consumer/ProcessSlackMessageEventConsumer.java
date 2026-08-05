package com.sparta.logistics.notification.infrastructure.messaging.consumer;

import com.sparta.logistics.notification.application.command.usecase.TransmitSlackMessageUseCase;
import com.sparta.logistics.notification.infrastructure.messaging.event.ProcessSlackMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessSlackMessageEventConsumer {
    private final TransmitSlackMessageUseCase transmitSlackMessageUseCase;

    @RabbitListener(queues = "${message.queue.notification}")
    public void consume(ProcessSlackMessageEvent event) {
        transmitSlackMessageUseCase.transmit(event.toCommand());
    }
}
