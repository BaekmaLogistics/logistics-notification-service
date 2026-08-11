package com.sparta.logistics.notification.application.command.model;

import java.util.UUID;

public record CompanyInfo(
        UUID id,
        String name,
        String type,
        UUID hubId,
        String address
) {
}
