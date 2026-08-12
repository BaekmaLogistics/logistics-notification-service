package com.sparta.logistics.notification.application.query.service;

import com.sparta.logistics.notification.application.query.dto.SlackMessageInfo;
import com.sparta.logistics.notification.application.query.usecase.GetSlackMessageUseCase;
import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.domain.repository.SlackMessageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class GetSlackMessageService implements GetSlackMessageUseCase {
    private final SlackMessageQueryRepository slackMessageQueryRepository;

    @Override
    public SlackMessageInfo getSlackMessage(UUID slackMessageId) {
        return slackMessageQueryRepository
                .findById(slackMessageId)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.SLACK_MESSAGE_NOT_FOUND));
    }
}
