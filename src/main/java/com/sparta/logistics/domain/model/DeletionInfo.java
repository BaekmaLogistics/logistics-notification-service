package com.sparta.logistics.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DeletionInfo(
        Instant deletedAt,
        UUID deletedBy
) {
}
