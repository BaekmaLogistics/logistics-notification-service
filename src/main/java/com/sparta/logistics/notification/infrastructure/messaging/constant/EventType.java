package com.sparta.logistics.notification.infrastructure.messaging.constant;

import com.sparta.logistics.notification.infrastructure.messaging.event.OrderCompletedPayload;
import com.sparta.logistics.notification.infrastructure.messaging.event.TransmitSlackMessagePayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum EventType {
    ORDER_COMPLETED("OrderCompleted", OrderCompletedPayload.class),
    TRANSMIT_SLACK_MESSAGE("TransmitSlackMessage", TransmitSlackMessagePayload.class),
    UNDEFINED("Undefined", null);

    private static final Map<String, EventType> EVENT_KEY_MAP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            EventType::getKey,
                            eventType -> eventType
                    ));

    public static EventType fromKeyString(String eventTypeString) {
        if (eventTypeString == null) {
            return UNDEFINED;
        }
        return EVENT_KEY_MAP.getOrDefault(eventTypeString, UNDEFINED);
    }

    private final String key;
    private final Class<?> payloadClass;
}
