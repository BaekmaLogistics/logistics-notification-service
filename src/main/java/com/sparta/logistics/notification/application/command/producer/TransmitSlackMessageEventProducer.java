package com.sparta.logistics.notification.application.command.producer;

import java.util.UUID;

public interface TransmitSlackMessageEventProducer {
    void produce(UUID slackMessageId, UUID actorId);
}
