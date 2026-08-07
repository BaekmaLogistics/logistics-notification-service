package com.sparta.logistics.notification.presentation.command.controller;

import com.sparta.logistics.notification.application.command.usecase.DeleteSlackMessageUseCase;
import com.sparta.logistics.notification.application.command.usecase.SendSlackMessageUseCase;
import com.sparta.logistics.notification.application.command.usecase.UpdateSlackMessageUseCase;
import com.sparta.logistics.notification.common.code.GeneralResponseCode;
import com.sparta.logistics.notification.common.security.AuthUser;
import com.sparta.logistics.notification.presentation.command.dto.SendSlackMessageRequest;
import com.sparta.logistics.notification.presentation.command.dto.UpdateSlackMessageRequest;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'SUPPLIER_MANAGER')")
    @PostMapping("/v1/slack-messages")
    public ResponseEntity<GeneralResponse<Void>> sendSlackMessage(
            @AuthenticationPrincipal AuthUser user,
            @RequestBody @Valid SendSlackMessageRequest request
    ) {
        sendSlackMessageUseCase.sendMessage(request.toCommand(user.id()), user.id());

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.ACCEPTED, null
        );
    }

    @PreAuthorize("hasAnyRole('MASTER')")
    @PatchMapping("/v1/slack-messages/{slackMessageId}")
    public ResponseEntity<GeneralResponse<Void>> updateSlackMessage(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable UUID slackMessageId,
            @RequestBody @Valid UpdateSlackMessageRequest request
    ) {
        updateSlackMessageUseCase.update(slackMessageId, request.toCommand(), user.id());

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK, null
        );
    }

    @PreAuthorize("hasAnyRole('MASTER')")
    @DeleteMapping("/v1/slack-messages/{slackMessageId}")
    public ResponseEntity<GeneralResponse<Void>> deleteSlackMessage(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable UUID slackMessageId
    ) {
        deleteSlackMessageUseCase.delete(slackMessageId, user.id());

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK, null
        );
    }
}