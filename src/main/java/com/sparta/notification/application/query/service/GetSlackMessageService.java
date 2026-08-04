package com.sparta.notification.application.query.service;

import com.sparta.notification.application.query.dto.SlackMessageInfo;
import com.sparta.notification.application.query.usecase.GetSlackMessagesUseCase;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetSlackMessageService implements GetSlackMessagesUseCase {
    @Override
    public SlackMessageInfo getSlackMessage(UUID slackMessageId, UUID userId) {
        return null;
    }
}
