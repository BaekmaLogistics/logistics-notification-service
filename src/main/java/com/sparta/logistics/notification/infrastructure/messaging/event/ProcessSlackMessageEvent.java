package com.sparta.logistics.notification.infrastructure.messaging.event;

import com.sparta.logistics.notification.application.command.dto.TransmitSlackMessageCommand;
import com.sparta.logistics.notification.application.command.dto.SendSlackMessageCommand;

import java.util.UUID;

public record ProcessSlackMessageEvent(
        UUID receiverId,
        UUID senderId,
        String content
) {
    public static ProcessSlackMessageEvent from(SendSlackMessageCommand command, UUID senderId) {
        return new ProcessSlackMessageEvent(
                command.receiverId(),
                senderId,
                command.content()
        );
    }

    public TransmitSlackMessageCommand toCommand() {
        return new TransmitSlackMessageCommand(
                receiverId,
                senderId,
                content
        );
    }
}
