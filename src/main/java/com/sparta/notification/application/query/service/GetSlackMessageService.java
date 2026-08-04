package com.sparta.notification.application.query.service;

import com.sparta.notification.application.query.dto.SlackMessageInfo;
import com.sparta.notification.application.query.usecase.GetSlackMessageUseCase;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetSlackMessageService implements GetSlackMessageUseCase {
    @Override
    public SlackMessageInfo getMessage(UUID slackMessageId, UUID userId) {
        return null;
    }
}
