package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.client.UserServiceClient;
import com.sparta.logistics.notification.application.command.dto.SendSlackMessageCommand;
import com.sparta.logistics.notification.application.command.dto.UpdateSlackMessageCommand;
import com.sparta.logistics.notification.application.command.dto.UserInfo;
import com.sparta.logistics.notification.application.command.usecase.DeleteSlackMessageUseCase;
import com.sparta.logistics.notification.application.command.usecase.UpdateSlackMessageUseCase;
import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.domain.model.SlackMessageStatus;
import com.sparta.logistics.notification.domain.repository.SlackMessageCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class SlackMessageCommandService implements UpdateSlackMessageUseCase, DeleteSlackMessageUseCase {
    private final SlackMessageCommandRepository slackMessageCommandRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    public SlackMessage append(SendSlackMessageCommand command, UUID senderId) {
        Map<UUID, UserInfo> userInfos = userServiceClient
                .searchUserSlackInfos(List.of(command.receiverId(), senderId));

        UserInfo receiverInfo = userInfos.get(command.receiverId());
        UserInfo senderInfo = userInfos.get(senderId);

        if (receiverInfo == null || senderInfo == null) {
            throw new ApiException(ErrorResponseCode.FEIGN_CLIENT_ERROR, "수신자 또는 발신자의 유저 정보를 찾을 수 없습니다.");
        }

        SlackMessage slackMessage = SlackMessage.create(
                command.receiverId(),
                receiverInfo.slackId(),
                senderId,
                senderInfo.slackId(),
                command.content()
        );

        return slackMessageCommandRepository.append(slackMessage);
    }

    @Transactional
    public SlackMessage updateStatusToProcessing(UUID slackMessageId, UUID actorId) {
        SlackMessage slackMessage = getSlackMessage(slackMessageId);

        if (slackMessage.status() == SlackMessageStatus.SUCCESS) {
            throw new ApiException(ErrorResponseCode.INVALID_REQUEST, "이미 전송된 메세지입니다.");
        }

        if (slackMessage.status() == SlackMessageStatus.PROCESSING) {
            throw new ApiException(ErrorResponseCode.INVALID_REQUEST, "이미 처리 중인 메세지입니다.");
        }

        SlackMessage processing = slackMessage.process(actorId);
        slackMessageCommandRepository.update(processing);
        return processing;
    }

    @Transactional
    public void updateStatusToSuccess(UUID slackMessageId, UUID actorId) {
        SlackMessage slackMessage = getSlackMessage(slackMessageId);
        SlackMessage completed = slackMessage.complete(actorId);
        slackMessageCommandRepository.update(completed);
    }

    @Transactional
    public void updateStatusToRetrying(UUID slackMessageId, UUID actorId) {
        SlackMessage slackMessage = getSlackMessage(slackMessageId);
        SlackMessage retrying = slackMessage.retry(actorId);
        slackMessageCommandRepository.update(retrying);
    }

    @Transactional
    public void updateStatusToFailed(UUID slackMessageId, String errorMessage, UUID actorId) {
        SlackMessage slackMessage = getSlackMessage(slackMessageId);
        SlackMessage failed = slackMessage.fail(errorMessage, actorId);
        slackMessageCommandRepository.update(failed);
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
