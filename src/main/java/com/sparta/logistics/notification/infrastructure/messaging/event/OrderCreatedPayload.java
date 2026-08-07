package com.sparta.logistics.notification.infrastructure.messaging.event;

import com.sparta.logistics.notification.application.command.dto.SendOrderNotificationCommand;

import java.time.Instant;
import java.util.UUID;

public record OrderCreatedPayload(
        UUID orderId,
        UUID ordererId,
        UUID productId,
        UUID departureHubId,
        UUID waypointHubId,
        UUID deliveryDriverId,
        Instant occurredAt
) {
    public SendOrderNotificationCommand toCommand() {
        return new SendOrderNotificationCommand(
                this.orderId,
                this.ordererId,
                this.productId,
                this.departureHubId,
                this.waypointHubId,
                this.deliveryDriverId,
                this.occurredAt
        );
    }
}
