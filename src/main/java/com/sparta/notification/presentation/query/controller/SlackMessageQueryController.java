package com.sparta.notification.presentation.query.controller;

import com.sparta.notification.application.query.dto.SearchSlackMessageQuery;
import com.sparta.notification.application.query.dto.SimpleSlackMessageInfo;
import com.sparta.notification.application.query.dto.SlackMessageInfo;
import com.sparta.notification.application.query.usecase.GetSlackMessageUseCase;
import com.sparta.notification.application.query.usecase.SearchSlackMessagesUseCase;
import com.sparta.notification.common.code.GeneralResponseCode;
import com.sparta.notification.presentation.common.dto.response.GeneralResponse;
import com.sparta.notification.presentation.query.dto.SearchSlackMessageRequest;
import com.sparta.notification.presentation.query.dto.SimpleSlackMessageResponse;
import com.sparta.notification.presentation.query.dto.SlackMessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SlackMessageQueryController {
    private final SearchSlackMessagesUseCase searchSlackMessagesUseCase;
    private final GetSlackMessageUseCase getSlackMessageUseCase;

    @GetMapping("/v1/slack-messages")
    public ResponseEntity<GeneralResponse<Page<SimpleSlackMessageResponse>>> searchSlackMessages(
            @AuthenticationPrincipal UUID userId,
            @Valid SearchSlackMessageRequest request,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        SearchSlackMessageQuery query = request.toQuery();

        Page<SimpleSlackMessageInfo> slackMessages =
                searchSlackMessagesUseCase.searchMessages(query, pageable, userId);

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK, slackMessages.map(SimpleSlackMessageResponse::from)
        );
    }

    @GetMapping("/v1/slack-messages/{slackMessageId}")
    public ResponseEntity<GeneralResponse<SlackMessageResponse>> getSlackMessage(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID slackMessageId
    ) {
        SlackMessageInfo slackMessage =
                getSlackMessageUseCase.getMessage(slackMessageId, userId);

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK, SlackMessageResponse.from(slackMessage)
        );
    }
}