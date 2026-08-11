package com.sparta.logistics.notification.presentation.query.dto;

import com.sparta.logistics.notification.application.query.dto.SearchSlackMessageQuery;

import java.time.Instant;
import java.util.UUID;

public record SearchSlackMessageRequest(
        UUID receiverId,
        UUID senderId,
        String keyword,
        Instant startDate,
        Instant endDate
) {
    public SearchSlackMessageQuery toQuery() {
        return new SearchSlackMessageQuery(
                this.receiverId,
                this.senderId,
                this.keyword,
                this.startDate,
                this.endDate
        );
    }
}
