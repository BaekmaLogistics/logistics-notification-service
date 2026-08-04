package com.sparta.notification.presentation.command.controller;

import com.sparta.notification.application.command.usecase.SendSlackMessageUseCase;
import com.sparta.notification.common.code.GeneralResponseCode;
import com.sparta.notification.presentation.command.dto.SendSlackMessageRequest;
import com.sparta.notification.presentation.common.dto.response.GeneralResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
}

/*
    Notification	슬랙 메세지 전송	POST	/api/v1/notifications		Company Manager,Delivery Driver,Hub Driver,Hub Manager,Master
    Notification	슬랙 메세지 수정	PATCH	/api/v1/notifications/{notificationId}	notificationId	Master
    Notification	슬랙 메세지 삭제	DELETE	/api/v1/notifications/{notificationId}	notificationId	Master

 */