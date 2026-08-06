package com.sparta.logistics.notification.infrastructure.feign.dto;

import com.sparta.logistics.notification.application.command.dto.UserInfo;

import java.util.UUID;

public record UserFeignResponse(
        String name,
        String email,
        String slackId
) {
    public UserInfo toInfo(UUID userId) {
        return new UserInfo(userId, name, email, slackId);
    }
}
