package com.sparta.logistics.notification.infrastructure.feign.dto;

import com.sparta.logistics.notification.application.command.model.ProductInfo;

import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        UUID companyId
) {
    public ProductInfo toInfo() {
        return new ProductInfo(id, name, companyId);
    }
}
