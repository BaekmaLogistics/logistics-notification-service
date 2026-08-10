package com.sparta.logistics.notification.application.command.model;

import java.util.UUID;

public record HubInfo(
        UUID id,
        String name,
        String address,
        double latitude,
        double longitude,
        UUID managerId
) {
}