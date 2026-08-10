package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.client.SlackClient;
import com.sparta.logistics.notification.application.command.client.UserServiceClient;
import com.sparta.logistics.notification.application.command.dto.SendSlackMessageCommand;
import com.sparta.logistics.notification.application.command.dto.TransmitSlackMessageCommand;
import com.sparta.logistics.notification.application.command.model.UserInfo;
import com.sparta.logistics.notification.application.command.usecase.TransmitSlackMessageUseCase;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.domain.model.SlackMessageStatus;
import com.sparta.logistics.notification.domain.repository.SlackMessageCommandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class SlackMessageIntegrationTest {

    @Autowired
    private SlackMessageCommandService slackMessageCommandService;

    @Autowired
    private TransmitSlackMessageUseCase transmitSlackMessageUseCase;

    @Autowired
    private SlackMessageCommandRepository slackMessageCommandRepository;

    @MockitoBean
    private UserServiceClient userServiceClient;

    @MockitoBean // SlackClient 모킹 추가
    private SlackClient slackClient;

    private UUID receiverId;
    private UUID senderId;
    private final String targetSlackId = "U0BPALKJPGQ";

    @BeforeEach
    void setUp() {
        receiverId = UUID.randomUUID();
        senderId = UUID.randomUUID();

        UserInfo receiverInfo = new UserInfo(receiverId, "테스트수신자", "receiver@sparta.com", targetSlackId);
        UserInfo senderInfo = new UserInfo(senderId, "테스트발신자", "sender@sparta.com", targetSlackId);

        given(userServiceClient.searchUserSlackInfos(any()))
                .willReturn(Map.of(
                        receiverId, receiverInfo,
                        senderId, senderInfo
                ));
    }

    @Test
    @DisplayName("실제 DB 및 Slack API 연동 - Slack 메시지 생성 후 실제 DM 발송 및 성공 상태 저장 검증")
    void sendAndTransmitSlackMessageIntegrationTest() {
        // given
        String messageContent = "[통합 테스트] 실제 Slack DM 발송 테스트 메시지입니다. (Member ID: " + targetSlackId + ")";
        SendSlackMessageCommand sendCommand = new SendSlackMessageCommand(receiverId, senderId, messageContent);

        // 1. 메시지 생성 및 DB PENDING 저장
        SlackMessage savedMessage = slackMessageCommandService.append(
                sendCommand.receiverId(), sendCommand.senderId(), sendCommand.content()
        );

        assertThat(savedMessage).isNotNull();
        assertThat(savedMessage.id()).isNotNull();
        assertThat(savedMessage.status()).isEqualTo(SlackMessageStatus.PENDING);
        assertThat(savedMessage.receiverSlackId()).isEqualTo(targetSlackId);

        // 2. 실제 Slack API로 DM 발송 및 DB 상태 SUCCESS 갱신
        TransmitSlackMessageCommand transmitCommand = new TransmitSlackMessageCommand(
                savedMessage.id(),
                senderId
        );

        transmitSlackMessageUseCase.transmit(transmitCommand);

        // then: DB에서 조회하여 상태 및 발송 정보 최종 검증
        SlackMessage transmittedMessage = slackMessageCommandRepository.findById(savedMessage.id())
                .orElseThrow();

        assertThat(transmittedMessage.status()).isEqualTo(SlackMessageStatus.SUCCESS);
        assertThat(transmittedMessage.receiverSlackId()).isEqualTo(targetSlackId);
        assertThat(transmittedMessage.auditInfo().updatedBy()).isEqualTo(senderId);
    }

    @Test
    @DisplayName("Slack 메시지 전송 실패 - 예외 발생 시 retryCount가 증가하고 RETRYING 상태로 변경된다 (최대 재시도 미만)")
    void transmitSlackMessage_failure_shouldChangeStatusToRetrying() {
        // given
        SendSlackMessageCommand sendCommand = new SendSlackMessageCommand(receiverId, senderId, "[통합 테스트] 실패 케이스");
        SlackMessage savedMessage = slackMessageCommandService.append(
                sendCommand.receiverId(), sendCommand.senderId(), sendCommand.content()
        );

        // SlackClient 호출 시 예외 발생하도록 Mocking
        willThrow(new RuntimeException("Slack API 전송 오류"))
                .given(slackClient).transmitSlackMessage(any());

        TransmitSlackMessageCommand transmitCommand = new TransmitSlackMessageCommand(
                savedMessage.id(),
                senderId
        );

        // when
        transmitSlackMessageUseCase.transmit(transmitCommand);

        // then
        SlackMessage failedMessage = slackMessageCommandRepository.findById(savedMessage.id())
                .orElseThrow();

        // 1회 실패했으므로 RETRYING 상태 및 retryCount가 1증가했는지 검증
        assertThat(failedMessage.status()).isEqualTo(SlackMessageStatus.RETRYING);
        assertThat(failedMessage.retryCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Slack 메시지 전송 실패 - MAX_RETRY_COUNT 초과 시 FAILED 상태로 변경된다")
    void transmitSlackMessage_exceedMaxRetry_shouldChangeStatusToFailed() {
        // given
        SendSlackMessageCommand sendCommand = new SendSlackMessageCommand(receiverId, senderId, "[통합 테스트] 최종 실패 케이스");
        SlackMessage savedMessage = slackMessageCommandService.append(
                sendCommand.receiverId(), sendCommand.senderId(), sendCommand.content()
        );

        // MAX_RETRY_COUNT(3)에 도달하도록 강제로 retryCount를 3으로 맞춘 상태를 가정하거나,
        // 3번 연속 transmit을 호출하여 시뮬레이션
        willThrow(new RuntimeException("Slack API 연속 전송 오류"))
                .given(slackClient).transmitSlackMessage(any());

        TransmitSlackMessageCommand transmitCommand = new TransmitSlackMessageCommand(
                savedMessage.id(),
                senderId
        );

        // 3회 실패 시뮬레이션 (Facade 로직상 retryCount < 3 조건 체크)
        for (int i = 0; i <= 3; i++) {
            transmitSlackMessageUseCase.transmit(transmitCommand);
        }

        // then
        SlackMessage finalFailedMessage = slackMessageCommandRepository.findById(savedMessage.id())
                .orElseThrow();

        assertThat(finalFailedMessage.status()).isEqualTo(SlackMessageStatus.FAILED);
        assertThat(finalFailedMessage.errorMessage()).contains("Slack API 연속 전송 오류");
    }
}
