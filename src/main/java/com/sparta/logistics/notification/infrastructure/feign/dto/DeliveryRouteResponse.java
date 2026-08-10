package com.sparta.logistics.notification.infrastructure.feign.dto;

import com.sparta.logistics.notification.application.command.model.DeliveryRouteInfo;

import java.util.UUID;

public record DeliveryRouteResponse(
        UUID id,
        Integer sequence,
        UUID fromHubId,
        UUID toHubId,
        Double expectedDistance,
        Integer expectedDuration,
        Double actualDistance,
        Integer actualDuration,
        UUID driverId
) {
    public DeliveryRouteInfo toInfo() {
        return new DeliveryRouteInfo(
                id,
                sequence,
                fromHubId,
                toHubId,
                expectedDistance,
                expectedDuration,
                actualDistance,
                actualDuration,
                driverId
        );
    }
}
