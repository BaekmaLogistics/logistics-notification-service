package com.sparta.logistics.notification.presentation.query.controller;

import com.sparta.logistics.notification.application.query.dto.SearchSlackMessageQuery;
import com.sparta.logistics.notification.application.query.dto.SimpleSlackMessageInfo;
import com.sparta.logistics.notification.application.query.dto.SlackMessageInfo;
import com.sparta.logistics.notification.application.query.usecase.GetSlackMessageUseCase;
import com.sparta.logistics.notification.application.query.usecase.SearchSlackMessagesUseCase;
import com.sparta.logistics.notification.common.code.GeneralResponseCode;
import com.sparta.logistics.notification.common.security.AuthUser;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import com.sparta.logistics.notification.presentation.query.dto.SearchSlackMessageRequest;
import com.sparta.logistics.notification.presentation.query.dto.SimpleSlackMessageResponse;
import com.sparta.logistics.notification.presentation.query.dto.SlackMessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('MASTER')")
    @GetMapping("/v1/slack-messages")
    public ResponseEntity<GeneralResponse<Page<SimpleSlackMessageResponse>>> searchSlackMessages(
            @AuthenticationPrincipal AuthUser user,
            @Valid SearchSlackMessageRequest request,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        SearchSlackMessageQuery query = request.toQuery();

        Page<SimpleSlackMessageInfo> slackMessages =
                searchSlackMessagesUseCase.searchMessages(query, pageable);

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK, slackMessages.map(SimpleSlackMessageResponse::from)
        );
    }

    @PreAuthorize("hasAnyRole('MASTER')")
    @GetMapping("/v1/slack-messages/{slackMessageId}")
    public ResponseEntity<GeneralResponse<SlackMessageResponse>> getSlackMessage(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable UUID slackMessageId
    ) {
        SlackMessageInfo slackMessage =
                getSlackMessageUseCase.getSlackMessage(slackMessageId);

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK, SlackMessageResponse.from(slackMessage)
        );
    }
}