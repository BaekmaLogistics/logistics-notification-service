package com.sparta.logistics.notification.infrastructure.feign.dto;

import com.sparta.logistics.notification.application.command.model.OrderInfo;

import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID departureHubId,
        UUID receiverCompanyId,
        UUID productId,
        int quantity,
        UUID deliveryId,
        String orderStatus,
        String requestMessage,
        Instant dueDate,
        Instant canceledAt,
        String canceledReason
) {
    public OrderInfo toInfo() {
        return new OrderInfo(
                id,
                departureHubId,
                receiverCompanyId,
                productId,
                quantity,
                deliveryId,
                orderStatus,
                requestMessage,
                dueDate,
                canceledAt,
                canceledReason
        );
    }
}
