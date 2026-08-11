package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.client.UserServiceClient;
import com.sparta.logistics.notification.application.command.dto.UpdateSlackMessageCommand;
import com.sparta.logistics.notification.application.command.model.UserInfo;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.domain.model.SlackMessageStatus;
import com.sparta.logistics.notification.domain.repository.SlackMessageCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SlackMessageCommandServiceTest {

    @InjectMocks
    private SlackMessageCommandService slackMessageCommandService;

    @Mock
    private SlackMessageCommandRepository slackMessageCommandRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Test
    @DisplayName("메시지 저장(append) - 수신자/발신자 정보 조회 후 메시지 생성 및 저장 성공")
    void append_success() {
        // given
        UUID receiverId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String content = "안녕하세요";

        UserInfo receiverInfo = new UserInfo(receiverId, "수신자", "rec@test.com", "U_REC");
        UserInfo senderInfo = new UserInfo(senderId, "발신자", "sen@test.com", "U_SEN");

        given(userServiceClient.searchUserSlackInfos(List.of(receiverId, senderId)))
                .willReturn(Map.of(receiverId, receiverInfo, senderId, senderInfo));

        SlackMessage createdMessage = mock(SlackMessage.class);
        given(slackMessageCommandRepository.append(any(SlackMessage.class))).willReturn(createdMessage);

        // when
        SlackMessage result = slackMessageCommandService.append(receiverId, senderId, content);

        // then
        assertThat(result).isNotNull();
        then(slackMessageCommandRepository).should().append(any(SlackMessage.class));
    }

    @Test
    @DisplayName("메시지 저장(append) - 발신자(senderId)가 null인 경우 수신자 정보만 조회 후 성공")
    void append_nullSender_success() {
        // given
        UUID receiverId = UUID.randomUUID();
        String content = "시스템 메시지";

        UserInfo receiverInfo = new UserInfo(receiverId, "수신자", "rec@test.com", "U_REC");

        given(userServiceClient.searchUserSlackInfos(List.of(receiverId)))
                .willReturn(Map.of(receiverId, receiverInfo));

        SlackMessage createdMessage = mock(SlackMessage.class);
        given(slackMessageCommandRepository.append(any(SlackMessage.class))).willReturn(createdMessage);

        // when
        SlackMessage result = slackMessageCommandService.append(receiverId, null, content);

        // then
        assertThat(result).isNotNull();
        then(slackMessageCommandRepository).should().append(any(SlackMessage.class));
    }

    @Test
    @DisplayName("메시지 저장(append) - 사용자 정보 누락 시 ApiException 발생")
    void append_userNotFound_throwsApiException() {
        // given
        UUID receiverId = UUID.randomUUID();
        given(userServiceClient.searchUserSlackInfos(List.of(receiverId)))
                .willReturn(Map.of());

        // when & then
        assertThatThrownBy(() -> slackMessageCommandService.append(receiverId, null, "내용"))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("상태 변경(updateStatusToProcessing) - 정상 상태 변경 성공")
    void updateStatusToProcessing_success() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        SlackMessage slackMessage = SlackMessage.create(UUID.randomUUID(), "U_REC", null, null, "내용");

        given(slackMessageCommandRepository.findById(messageId)).willReturn(Optional.of(slackMessage));

        // when
        SlackMessage result = slackMessageCommandService.updateStatusToProcessing(messageId, actorId);

        // then
        assertThat(result.status()).isEqualTo(SlackMessageStatus.PROCESSING);
        then(slackMessageCommandRepository).should().update(any(SlackMessage.class));
    }

    @Test
    @DisplayName("상태 변경(updateStatusToProcessing) - 이미 SUCCESS 상태인 경우 예외 발생")
    void updateStatusToProcessing_alreadySuccess_throwsException() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        SlackMessage slackMessage = mock(SlackMessage.class);

        given(slackMessage.status()).willReturn(SlackMessageStatus.SUCCESS);
        given(slackMessageCommandRepository.findById(messageId)).willReturn(Optional.of(slackMessage));

        // when & then
        assertThatThrownBy(() -> slackMessageCommandService.updateStatusToProcessing(messageId, actorId))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("상태 변경(updateStatusToProcessing) - 이미 PROCESSING 상태인 경우 예외 발생")
    void updateStatusToProcessing_alreadyProcessing_throwsException() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        SlackMessage slackMessage = mock(SlackMessage.class);

        given(slackMessage.status()).willReturn(SlackMessageStatus.PROCESSING);
        given(slackMessageCommandRepository.findById(messageId)).willReturn(Optional.of(slackMessage));

        // when & then
        assertThatThrownBy(() -> slackMessageCommandService.updateStatusToProcessing(messageId, actorId))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("상태 변경(updateStatusToSuccess) - 수신 상태 업데이트 후 repository.update 호출")
    void updateStatusToSuccess_success() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        SlackMessage slackMessage = SlackMessage.create(UUID.randomUUID(), "U_REC", null, null, "내용");
        slackMessage = slackMessage.process(UUID.randomUUID());

        given(slackMessageCommandRepository.findById(messageId)).willReturn(Optional.of(slackMessage));

        // when
        slackMessageCommandService.updateStatusToSuccess(messageId, actorId);

        // then
        then(slackMessageCommandRepository).should().update(any(SlackMessage.class));
    }

    @Test
    @DisplayName("상태 변경(updateStatusToSuccess) - 메시지 미존재 시 예외 발생")
    void updateStatusToSuccess_notFound_throwsApiException() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();

        given(slackMessageCommandRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> slackMessageCommandService.updateStatusToSuccess(messageId, actorId))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("상태 변경(updateStatusToRetrying) - 재시도 상태 업데이트 후 repository.update 호출")
    void updateStatusToRetrying_success() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        SlackMessage slackMessage = SlackMessage.create(UUID.randomUUID(), "U_REC", null, null, "내용");
        slackMessage = slackMessage.process(UUID.randomUUID());

        given(slackMessageCommandRepository.findById(messageId)).willReturn(Optional.of(slackMessage));

        // when
        slackMessageCommandService.updateStatusToRetrying(messageId, actorId);

        // then
        then(slackMessageCommandRepository).should().update(any(SlackMessage.class));
    }

    @Test
    @DisplayName("상태 변경(updateStatusToFailed) - 실패 상태 업데이트 후 repository.update 호출")
    void updateStatusToFailed_success() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        SlackMessage slackMessage = SlackMessage.create(UUID.randomUUID(), "U_REC", null, null, "내용");
        slackMessage = slackMessage.process(UUID.randomUUID());

        given(slackMessageCommandRepository.findById(messageId)).willReturn(Optional.of(slackMessage));

        // when
        slackMessageCommandService.updateStatusToFailed(messageId, "오류 메시지", actorId);

        // then
        then(slackMessageCommandRepository).should().update(any(SlackMessage.class));
    }

    @Test
    @DisplayName("메시지 수정(update) - 내용 변경 후 저장소 update 호출")
    void update_success() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UpdateSlackMessageCommand command = new UpdateSlackMessageCommand("수정된 메시지");

        SlackMessage existingMessage = SlackMessage.create(UUID.randomUUID(), "U_REC", UUID.randomUUID(), "U_SEN", "이전 메시지");
        given(slackMessageCommandRepository.findById(messageId)).willReturn(Optional.of(existingMessage));

        // when
        slackMessageCommandService.update(messageId, command, userId);

        // then
        then(slackMessageCommandRepository).should().update(any(SlackMessage.class));
    }

    @Test
    @DisplayName("메시지 수정(update) - 메시지가 존재하지 않는 경우 ApiException 발생")
    void update_notFound_throwsApiException() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UpdateSlackMessageCommand command = new UpdateSlackMessageCommand("수정된 메시지");

        given(slackMessageCommandRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> slackMessageCommandService.update(messageId, command, userId))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("메시지 삭제(delete) - 저장소 delete 호출")
    void delete_success() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when
        slackMessageCommandService.delete(messageId, userId);

        // then
        then(slackMessageCommandRepository).should().delete(messageId, userId);
    }

    @Test
    @DisplayName("단건 조회(getSlackMessage) - 성공 케이스")
    void getSlackMessage_success() {
        // given
        UUID messageId = UUID.randomUUID();
        SlackMessage slackMessage = mock(SlackMessage.class);
        given(slackMessageCommandRepository.findById(messageId)).willReturn(Optional.of(slackMessage));

        // when
        SlackMessage result = slackMessageCommandService.getSlackMessage(messageId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("단건 조회(getSlackMessage) - 존재하지 않을 경우 ApiException 발생")
    void getSlackMessage_notFound_throwsApiException() {
        // given
        UUID messageId = UUID.randomUUID();
        given(slackMessageCommandRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> slackMessageCommandService.getSlackMessage(messageId))
                .isInstanceOf(ApiException.class);
    }
}
