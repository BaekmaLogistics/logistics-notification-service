package com.sparta.logistics.notification.application.command.producer;

import com.sparta.logistics.notification.application.command.dto.SendSlackMessageCommand;

import java.util.UUID;

public interface TransmitSlackMessageEventProducer {
    void produce(SendSlackMessageCommand command, UUID slackMessageId,UUID userId);
}
