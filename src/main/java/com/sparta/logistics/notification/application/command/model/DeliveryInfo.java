package com.sparta.logistics.notification.application.command.model;

import java.util.UUID;

public record DeliveryInfo(
        UUID id,
        UUID userId,
        String deliveryType,
        UUID hubId,
        int deliveryOrder
) {
}