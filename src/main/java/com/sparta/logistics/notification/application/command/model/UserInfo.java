package com.sparta.logistics.notification.application.command.model;

import java.util.UUID;

public record UserInfo(
        UUID id,
        String name,
        String email,
        String slackId
) {
}
