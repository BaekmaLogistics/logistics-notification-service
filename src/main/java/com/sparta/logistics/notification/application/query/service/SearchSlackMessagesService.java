package com.sparta.logistics.notification.application.query.service;

import com.sparta.logistics.notification.application.query.usecase.SearchSlackMessagesUseCase;
import com.sparta.logistics.notification.application.query.dto.SearchSlackMessageQuery;
import com.sparta.logistics.notification.application.query.dto.SimpleSlackMessageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class SearchSlackMessagesService implements SearchSlackMessagesUseCase {
    @Override
    public Page<SimpleSlackMessageInfo> searchMessages(
            SearchSlackMessageQuery query,
            Pageable pageable,
            UUID userId
    ) {
        return null;
    }
}
