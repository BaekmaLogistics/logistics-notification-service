package com.sparta.notification.application.command.dto;

import java.util.UUID;

public record SendSlackMessageCommand(
        UUID receiverId,
        String content
) {
}
