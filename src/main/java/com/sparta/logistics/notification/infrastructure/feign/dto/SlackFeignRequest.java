package com.sparta.logistics.notification.infrastructure.feign.dto;

public record SlackFeignRequest(
        String channel,
        String text
) {
}
