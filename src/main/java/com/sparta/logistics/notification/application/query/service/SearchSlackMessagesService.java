package com.sparta.logistics.notification.application.query.service;

import com.sparta.logistics.notification.application.query.dto.SearchSlackMessageQuery;
import com.sparta.logistics.notification.application.query.dto.SimpleSlackMessageInfo;
import com.sparta.logistics.notification.application.query.usecase.SearchSlackMessagesUseCase;
import com.sparta.logistics.notification.domain.repository.SlackMessageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class SearchSlackMessagesService implements SearchSlackMessagesUseCase {
    private final SlackMessageQueryRepository slackMessageQueryRepository;

    @Override
    public Page<SimpleSlackMessageInfo> searchMessages(
            SearchSlackMessageQuery query,
            Pageable pageable
    ) {
        return slackMessageQueryRepository.searchMessages(query, pageable);
    }
}
