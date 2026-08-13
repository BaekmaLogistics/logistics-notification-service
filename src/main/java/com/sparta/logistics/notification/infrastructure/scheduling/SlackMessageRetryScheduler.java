package com.sparta.logistics.notification.infrastructure.scheduling;

import com.sparta.logistics.notification.application.command.producer.TransmitSlackMessageEventProducer;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.domain.model.SlackMessageStatus;
import com.sparta.logistics.notification.domain.repository.SlackMessageCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackMessageRetryScheduler {

    private final SlackMessageCommandRepository commandRepository;
    private final TransmitSlackMessageEventProducer eventProducer;

    // 1분마다 미처리 상태(PENDING, RETRYING, PROCESSING) 메시지를 재처리
    @Scheduled(fixedDelay = 60000)
    public void retryPendingSlackMessages() {
        List<SlackMessageStatus> targetStatuses = List.of(
                SlackMessageStatus.PENDING,
                SlackMessageStatus.RETRYING,
                SlackMessageStatus.PROCESSING
        );

        List<SlackMessage> pendingMessages = commandRepository.findAllByStatusIn(targetStatuses);

        if (pendingMessages.isEmpty()) {
            return;
        }

        log.info("[SlackMessageRetryScheduler] 미처리 슬랙 메시지 {}건 재처리 시도", pendingMessages.size());

        for (SlackMessage message : pendingMessages) {
            try {
                // 발송자(senderId) 또는 기본 actorId로 재전송 이벤트 발행
                eventProducer.produce(message.id(), null);
            } catch (Exception e) {
                log.error("[SlackMessageRetryScheduler] 슬랙 메시지 재처리 실패. Message ID: {}", message.id(), e);
            }
        }
    }
}
