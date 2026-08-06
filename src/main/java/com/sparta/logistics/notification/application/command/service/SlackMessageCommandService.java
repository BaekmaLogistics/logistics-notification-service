package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.dto.SendSlackMessageCommand;
import com.sparta.logistics.notification.application.command.dto.UpdateSlackMessageCommand;
import com.sparta.logistics.notification.application.command.usecase.DeleteSlackMessageUseCase;
import com.sparta.logistics.notification.application.command.usecase.UpdateSlackMessageUseCase;
import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.domain.repository.SlackMessageCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class SlackMessageCommandService implements UpdateSlackMessageUseCase, DeleteSlackMessageUseCase {
    private final SlackMessageCommandRepository slackMessageCommandRepository;

    @Transactional
    public SlackMessage append(SendSlackMessageCommand command, UUID userId) {


        SlackMessage slackMessage = SlackMessage.create(
                command.receiverId(),
                null,
                userId,
                null,
                command.content()
        );

        return slackMessageCommandRepository.append(slackMessage);
    }

    @Override
    @Transactional
    public void update(UUID slackMessageId, UpdateSlackMessageCommand command, UUID userId) {
        SlackMessage slackMessage = getSlackMessage(slackMessageId);

        if (!slackMessage.senderId().equals(userId)) {
            throw new ApiException(ErrorResponseCode.INVALID_REQUEST, "본인이 작성한 메시지만 수정할 수 있습니다.");
        }

        SlackMessage updatedMessage = new SlackMessage(
                slackMessage.id(),
                slackMessage.receiverId(),
                command.receiverSlackId(),
                slackMessage.senderId(),
                command.senderSlackId(),
                command.content(),
                slackMessage.status(),
                slackMessage.retryCount(),
                slackMessage.errorMessage(),
                slackMessage.auditInfo(),
                slackMessage.deletionInfo()
        );

        slackMessageCommandRepository.update(updatedMessage);
    }

    @Override
    @Transactional
    public void delete(UUID slackMessageId, UUID userId) {
        SlackMessage slackMessage = getSlackMessage(slackMessageId);

        if (!slackMessage.senderId().equals(userId)) {
            throw new ApiException(ErrorResponseCode.INVALID_REQUEST, "본인이 작성한 메시지만 삭제할 수 있습니다.");
        }

        slackMessageCommandRepository.delete(slackMessageId, userId);
    }

    @Transactional(readOnly = true)
    public SlackMessage getSlackMessage(UUID slackMessageId) {
        return slackMessageCommandRepository.findById(slackMessageId)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.SLACK_MESSAGE_NOT_FOUND));
    }
}
