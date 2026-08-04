package com.sparta.notification.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DeletionInfo(
        Instant deletedAt,
        UUID deletedBy
) {
}
