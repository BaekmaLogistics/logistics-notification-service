package com.sparta.logistics.notification.application.query.dto;

import java.time.Instant;
import java.util.UUID;

public record SearchSlackMessageQuery(
        UUID receiverId,
        UUID senderId,
        String keyword,
        Instant startDate,
        Instant endDate
) {
}