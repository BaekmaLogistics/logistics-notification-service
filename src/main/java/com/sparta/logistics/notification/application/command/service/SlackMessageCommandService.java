package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.client.UserServiceClient;
import com.sparta.logistics.notification.application.command.dto.UpdateSlackMessageCommand;
import com.sparta.logistics.notification.application.command.model.UserInfo;
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
    public SlackMessage append(
            UUID receiverId,
            UUID senderId,
            String content
    ) {
        // senderId 존재 여부에 따라 조회 대상 사용자 ID 목록 구성 (List.of NPE 방지)
        List<UUID> targetUserIds = senderId == null
                ? List.of(receiverId)
                : List.of(receiverId, senderId);

        // 사용자 서비스(Feign)를 통해 수신자 및 발신자 정보 일괄 조회
        Map<UUID, UserInfo> userInfos = userServiceClient
                .searchUserSlackInfos(targetUserIds);

        UserInfo receiverInfo = userInfos.get(receiverId);
        UserInfo senderInfo = senderId != null ? userInfos.get(senderId) : null;

        // 수신자 필수 검증 및 발신자 존재 시 유효성 검증
        if (receiverInfo == null || (senderId != null && senderInfo == null)) {
            throw new ApiException(ErrorResponseCode.FEIGN_CLIENT_ERROR, "수신자 또는 발신자의 유저 정보를 찾을 수 없습니다.");
        }

        // 조회한 슬랙 정보 기반으로 메시지 도메인 엔티티 생성 및 저장
        SlackMessage slackMessage = SlackMessage.create(
                receiverId,
                receiverInfo.slackId(),
                senderId,
                senderInfo != null ? senderInfo.slackId() : null,
                content
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
        // TODO: PROCESSING 중 장애 발생으로 중단된 상태 처리 필요

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

        SlackMessage updatedMessage = new SlackMessage(
                slackMessage.id(),
                slackMessage.receiverId(),
                slackMessage.receiverSlackId(),
                slackMessage.senderId(),
                slackMessage.senderSlackId(),
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
        slackMessageCommandRepository.delete(slackMessageId, userId);
    }

    @Transactional(readOnly = true)
    public SlackMessage getSlackMessage(UUID slackMessageId) {
        return slackMessageCommandRepository.findById(slackMessageId)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.SLACK_MESSAGE_NOT_FOUND));
    }
}
