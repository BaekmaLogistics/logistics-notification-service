package com.sparta.logistics.notification.domain.model;

import java.time.Instant;
import java.util.UUID;

public record AuditInfo(
        Instant createdAt,
        UUID createdBy,
        Instant updatedAt,
        UUID updatedBy
) {
}
