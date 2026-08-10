package com.sparta.logistics.notification.infrastructure.feign.dto;

import com.sparta.logistics.notification.application.command.model.HubInfo;

import java.util.UUID;

public record HubResponse(
        UUID id,
        String name,
        String address,
        double latitude,
        double longitude,
        UUID managerId
) {
    public HubInfo toInfo() {
        return new HubInfo(id, name, address, latitude, longitude, managerId);
    }
}
