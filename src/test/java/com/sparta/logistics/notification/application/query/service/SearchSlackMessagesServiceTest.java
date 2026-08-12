package com.sparta.logistics.notification.application.query.service;

import com.sparta.logistics.notification.application.query.dto.SearchSlackMessageQuery;
import com.sparta.logistics.notification.application.query.dto.SimpleSlackMessageInfo;
import com.sparta.logistics.notification.domain.repository.SlackMessageQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SearchSlackMessagesServiceTest {

    @InjectMocks
    private SearchSlackMessagesService searchSlackMessagesService;

    @Mock
    private SlackMessageQueryRepository slackMessageQueryRepository;

    @Test
    @DisplayName("Slack 메시지 목록 검색 - 저장소 조회 결과 반환")
    void searchMessages_success() {
        // given
        SearchSlackMessageQuery query = mock(SearchSlackMessageQuery.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<SimpleSlackMessageInfo> expectedPage = mock(Page.class);

        given(slackMessageQueryRepository.searchMessages(query, pageable))
                .willReturn(expectedPage);

        // when
        Page<SimpleSlackMessageInfo> result = searchSlackMessagesService.searchMessages(query, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedPage);
    }
}
