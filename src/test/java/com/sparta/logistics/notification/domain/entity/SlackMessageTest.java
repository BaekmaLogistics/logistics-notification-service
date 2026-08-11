package com.sparta.logistics.notification.domain.entity;

import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.domain.model.SlackMessageStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SlackMessageTest {

    @Test
    @DisplayName("SlackMessage 생성 시 PENDING 상태 및 기본 값 정상 설정")
    void create_success() {
        // given
        UUID receiverId = UUID.randomUUID();
        String receiverSlackId = "U_REC";
        UUID senderId = UUID.randomUUID();
        String senderSlackId = "U_SEN";
        String content = "안녕하세요 테스트 메시지입니다.";

        // when
        SlackMessage slackMessage = SlackMessage.create(receiverId, receiverSlackId, senderId, senderSlackId, content);

        // then
        assertThat(slackMessage).isNotNull();
        assertThat(slackMessage.receiverId()).isEqualTo(receiverId);
        assertThat(slackMessage.receiverSlackId()).isEqualTo(receiverSlackId);
        assertThat(slackMessage.senderId()).isEqualTo(senderId);
        assertThat(slackMessage.senderSlackId()).isEqualTo(senderSlackId);
        assertThat(slackMessage.content()).isEqualTo(content);
        assertThat(slackMessage.status()).isEqualTo(SlackMessageStatus.PENDING);
        assertThat(slackMessage.retryCount()).isZero();
    }

    @Test
    @DisplayName("SlackMessage 생성 시 메시지 내용이 1000자를 초과하면 ApiException 예외 발생")
    void create_exceedContentLength_throwsApiException() {
        // given
        UUID receiverId = UUID.randomUUID();
        String longContent = "A".repeat(1001);

        // when & then
        assertThatThrownBy(() -> SlackMessage.create(receiverId, "U_REC", null, null, longContent))
                .isInstanceOf(ApiException.class);
    }
}
