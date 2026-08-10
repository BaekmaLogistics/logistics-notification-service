package com.sparta.logistics.notification.infrastructure.feign.dto;

import com.sparta.logistics.notification.application.command.model.UserInfo;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String slackId
) {
    public UserInfo toInfo() {
        return new UserInfo(id, name, email, slackId);
    }
}
