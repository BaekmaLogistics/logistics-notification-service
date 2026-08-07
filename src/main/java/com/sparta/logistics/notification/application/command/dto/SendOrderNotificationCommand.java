package com.sparta.logistics.notification.application.command.dto;

import java.time.Instant;
import java.util.UUID;

public record SendOrderNotificationCommand(
        UUID orderId,
        UUID ordererId,
        UUID productId,
        UUID departureHubId,
        UUID waypointHubId,
        UUID deliveryDriverId,
        Instant occurredAt
) {
}
