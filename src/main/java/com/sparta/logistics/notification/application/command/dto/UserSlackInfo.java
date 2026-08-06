package com.sparta.logistics.notification.application.command.dto;

import java.util.UUID;

public record UserSlackInfo(
        UUID userId,
        String name,
        String email,
        String slackId
) {
}
