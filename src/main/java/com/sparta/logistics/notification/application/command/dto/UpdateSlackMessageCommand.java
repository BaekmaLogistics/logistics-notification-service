package com.sparta.logistics.notification.application.command.dto;

public record UpdateSlackMessageCommand(
        String receiverSlackId,
        String senderSlackId,
        String content
) {
}
