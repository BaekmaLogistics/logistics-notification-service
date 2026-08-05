package com.sparta.notification.presentation.command.controller;

import com.sparta.notification.application.command.usecase.DeleteSlackMessageUseCase;
import com.sparta.notification.application.command.usecase.SendSlackMessageUseCase;
import com.sparta.notification.application.command.usecase.UpdateSlackMessageUseCase;
import com.sparta.notification.common.code.GeneralResponseCode;
import com.sparta.notification.presentation.command.dto.SendSlackMessageRequest;
import com.sparta.notification.presentation.command.dto.UpdateSlackMessageRequest;
import com.sparta.notification.presentation.common.dto.response.GeneralResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SlackMessageCommandController {
    private final SendSlackMessageUseCase sendSlackMessageUseCase;
    private final UpdateSlackMessageUseCase updateSlackMessageUseCase;
    private final DeleteSlackMessageUseCase deleteSlackMessageUseCase;

    @PostMapping("/v1/slack-messages")
    public ResponseEntity<GeneralResponse<Void>> sendSlackMessage(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid SendSlackMessageRequest request
    ) {
        sendSlackMessageUseCase.sendMessage(request.toCommand(), userId);

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.ACCEPTED, null
        );
    }

    @PatchMapping("/v1/slack-messages/{slackMessageId}")
    public ResponseEntity<GeneralResponse<Void>> updateSlackMessage(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID slackMessageId,
            @RequestBody @Valid UpdateSlackMessageRequest request
    ) {
        updateSlackMessageUseCase.updateMessage(slackMessageId, request.toCommand(), userId);

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK, null
        );
    }

    @DeleteMapping("/v1/slack-messages/{slackMessageId}")
    public ResponseEntity<GeneralResponse<Void>> deleteSlackMessage(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID slackMessageId
    ) {
        deleteSlackMessageUseCase.deleteMessage(slackMessageId, userId);

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK, null
        );
    }
}