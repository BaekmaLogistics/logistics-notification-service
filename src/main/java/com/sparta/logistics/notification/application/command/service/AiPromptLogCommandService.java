package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.domain.entity.AiHistory;
import org.springframework.stereotype.Service;

@Service
class AiPromptLogCommandService {
    public AiHistory append(String prompt) {
        return null;
    }

    public void updateStatusToSuccess(AiHistory aiHistory, String generatedMessage) {
    }

    public void updateStatusToRetrying(AiHistory aiHistory, int retryCount, String message) {
    }

    public void updateStatusToFailed(AiHistory aiHistory, String message) {
    }
}
