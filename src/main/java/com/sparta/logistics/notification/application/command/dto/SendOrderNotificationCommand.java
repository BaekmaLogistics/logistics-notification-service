package com.sparta.logistics.notification.application.command.dto;

import java.time.Instant;
import java.util.UUID;

public record SendOrderNotificationCommand(
        UUID orderId,
        UUID departureHubId,
        UUID receiverCompanyId,
        UUID productId,
        int quantity,
        UUID deliveryId,
        String orderStatus,
        String requestMessage,
        Instant dueDate,
        Instant canceledAt,
        String canceledReason,
        Instant occurredAt
) {
}
