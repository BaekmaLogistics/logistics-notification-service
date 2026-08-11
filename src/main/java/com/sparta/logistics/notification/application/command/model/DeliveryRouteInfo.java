package com.sparta.logistics.notification.application.command.model;

import java.util.UUID;

public record DeliveryRouteInfo (
        UUID id,
        Integer sequence,
        UUID fromHubId,
        UUID toHubId,
        Double expectedDistance,
        Integer expectedDuration,
        Double actualDistance,
        Integer actualDuration,
        UUID driverId
){
}
