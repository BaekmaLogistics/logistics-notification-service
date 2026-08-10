package com.sparta.logistics.notification.infrastructure.messaging.producer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventRoutingKey {
    TRANSMIT_SLACK_MESSAGE("notification.slack-message.transmit");

    private final String key;
}
