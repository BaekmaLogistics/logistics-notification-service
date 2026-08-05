package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.SendSlackMessageCommand;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class SlackMessageEventProducer {
    public void produceSlackEvent(SendSlackMessageCommand command, UUID userId) {
    }
}
