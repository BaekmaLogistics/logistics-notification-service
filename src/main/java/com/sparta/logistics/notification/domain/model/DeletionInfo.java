package com.sparta.logistics.notification.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DeletionInfo(
        Instant deletedAt,
        UUID deletedBy
) {
}
