package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.domain.entity.AiHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class AiPromptLogCommandServiceTest {

    private final AiPromptLogCommandService aiPromptLogCommandService = new AiPromptLogCommandService();

    @Test
    @DisplayName("AiPromptLogCommandService 기본 메서드 작동 확인")
    void testServiceMethods() {
        // when & then
        AiHistory result = aiPromptLogCommandService.append("test prompt");

        assertThatCode(() -> {
            aiPromptLogCommandService.updateStatusToSuccess(result, "generated");
            aiPromptLogCommandService.updateStatusToRetrying(result, 1, "retry error");
            aiPromptLogCommandService.updateStatusToFailed(result, "failed error");
        }).doesNotThrowAnyException();
    }
}
