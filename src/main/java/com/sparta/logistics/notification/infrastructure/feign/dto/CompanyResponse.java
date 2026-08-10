package com.sparta.logistics.notification.infrastructure.feign.dto;

import com.sparta.logistics.notification.application.command.model.CompanyInfo;

import java.util.UUID;

public record CompanyResponse(
        UUID id,
        String name,
        String type,
        UUID hubId,
        String address
) {
    public CompanyInfo toInfo() {
        return new CompanyInfo(id, name, type, hubId, address);
    }
}
