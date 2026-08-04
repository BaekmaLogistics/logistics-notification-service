package com.sparta.notification.application.query.usecase;

import com.sparta.notification.application.query.dto.SearchSlackMessageQuery;
import com.sparta.notification.application.query.dto.SimpleSlackMessageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SearchSlackMessagesUseCase {
    Page<SimpleSlackMessageInfo> searchMessages(SearchSlackMessageQuery query, Pageable pageable, UUID userId);
}
