package com.sparta.logistics.notification.infrastructure.feign.dto;

import com.sparta.logistics.notification.application.command.model.DeliveryInfo;

import java.util.UUID;

public record DeliveryResponse(
        UUID id,
        UUID userId,
        String deliveryType,
        UUID hubId,
        int deliveryOrder
) {
    public DeliveryInfo toInfo() {
        return new DeliveryInfo(id, userId, deliveryType, hubId, deliveryOrder);
    }
}
