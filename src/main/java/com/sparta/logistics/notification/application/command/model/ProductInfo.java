package com.sparta.logistics.notification.application.command.model;

import java.util.UUID;

public record ProductInfo(
        UUID id,
        String name,
        UUID companyId
) {
}