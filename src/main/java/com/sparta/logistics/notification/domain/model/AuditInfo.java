package com.sparta.logistics.notification.domain.model;

import java.time.Instant;
import java.util.UUID;

public record AuditInfo(
        Instant createdAt,
        UUID createdBy,
        Instant updatedAt,
        UUID updatedBy
) {
    public AuditInfo withUpdatedBy(UUID updatedBy) {
        return new AuditInfo(this.createdAt, this.createdBy, this.updatedAt,
                updatedBy);
    }
}
