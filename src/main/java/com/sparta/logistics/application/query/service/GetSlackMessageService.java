package com.sparta.logistics.application.query.service;

import com.sparta.logistics.application.query.dto.SlackMessageInfo;
import com.sparta.logistics.application.query.usecase.GetSlackMessageUseCase;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class GetSlackMessageService implements GetSlackMessageUseCase {
    @Override
    public SlackMessageInfo getMessage(UUID slackMessageId, UUID userId) {
        return null;
    }
}
