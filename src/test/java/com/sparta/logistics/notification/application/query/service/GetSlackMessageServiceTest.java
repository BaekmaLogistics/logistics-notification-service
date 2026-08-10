package com.sparta.logistics.notification.application.query.service;

import com.sparta.logistics.notification.application.query.dto.SlackMessageInfo;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.domain.repository.SlackMessageQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GetSlackMessageServiceTest {

    @InjectMocks
    private GetSlackMessageService getSlackMessageService;

    @Mock
    private SlackMessageQueryRepository slackMessageQueryRepository;

    @Test
    @DisplayName("Slack 메시지 단건 조회 - 메시지가 존재하면 반환")
    void getSlackMessage_success() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        SlackMessageInfo slackMessageInfo = mock(SlackMessageInfo.class);

        given(slackMessageQueryRepository.findByIdAndUserId(messageId, userId))
                .willReturn(Optional.of(slackMessageInfo));

        // when
        SlackMessageInfo result = getSlackMessageService.getSlackMessage(messageId, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(slackMessageInfo);
    }

    @Test
    @DisplayName("Slack 메시지 단건 조회 - 메시지가 존재하지 않으면 ApiException 발생")
    void getSlackMessage_notFound_throwsApiException() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        given(slackMessageQueryRepository.findByIdAndUserId(messageId, userId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getSlackMessageService.getSlackMessage(messageId, userId))
                .isInstanceOf(ApiException.class);
    }
}
