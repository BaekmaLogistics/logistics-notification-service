package com.sparta.notification.application.command.service;

import com.sparta.notification.application.command.dto.SendSlackMessageCommand;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class SlackMessageCommandService {
    public void save(SendSlackMessageCommand command, UUID userId) {
    }
}
