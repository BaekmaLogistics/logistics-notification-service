package com.sparta.logistics.notification.infrastructure.feign.dto;

public record SlackFeignResponse(
        boolean ok,
        String error
) {
}
