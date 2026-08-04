package com.sparta.notification.application.query.service;

import com.sparta.notification.application.query.usecase.SearchSlackMessagesUseCase;
import com.sparta.notification.application.query.dto.SearchSlackMessageQuery;
import com.sparta.notification.application.query.dto.SimpleSlackMessageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SearchSlackMessagesService implements SearchSlackMessagesUseCase {
    @Override
    public Page<SimpleSlackMessageInfo> searchMessages(
            SearchSlackMessageQuery query,
            Pageable pageable,
            UUID userId
    ) {
        return null;
    }
}
