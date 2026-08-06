package com.sparta.logistics.notification.infrastructure.feign.dto;

import com.sparta.logistics.notification.application.command.dto.UserSlackInfo;

import java.util.UUID;

public record UserFeignResponse(
        UUID userId,
        String name,
        String email,
        String slackId
) {
    public UserSlackInfo toInfo() {
        return new UserSlackInfo(userId, name, email, slackId);
    }
}
