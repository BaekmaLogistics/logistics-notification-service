package com.sparta.logistics.notification.application.query.service;

import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.domain.repository.SlackMessageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SlackMessageQueryService {
    private final SlackMessageQueryRepository slackMessageQueryRepository;

    public SlackMessage getSlackMessage(UUID slackMessageId){
        slackMessageQueryRepository.findById(slackMessageId)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.SLACK_MESSAGE_NOT_FOUND));
    }
}
