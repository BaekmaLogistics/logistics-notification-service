package com.sparta.logistics.notification.infrastructure.messaging.event;

import com.sparta.logistics.notification.application.command.dto.SendOrderNotificationCommand;

import java.time.Instant;
import java.util.UUID;

public record OrderCompletedPayload(
        UUID id, // 주문 ID
        UUID departureHubId, // 배송 출발 Hub ID
        UUID receiverCompanyId, // 수령 업체 ID
        UUID productId, // 상품 ID
        int quantity, // 주문 수량
        UUID deliveryId, // 배송 ID
        String orderStatus, // PENDING, DELIVERY_REQUESTED, DELIVERING, COMPLETED, CANCELED, FAILED
        String requestMessage, // 요청사항
        Instant dueDate, // 납품 기한 일시
        Instant canceledAt, // 주문 취소 일시
        String canceledReason, // 주문 취소 사유
        Instant occurredAt
) {
    public SendOrderNotificationCommand toCommand() {
        return new SendOrderNotificationCommand(
                this.id,
                this.departureHubId,
                this.receiverCompanyId,
                this.productId,
                this.quantity,
                this.deliveryId,
                this.orderStatus,
                this.requestMessage,
                this.dueDate,
                this.canceledAt,
                this.canceledReason,
                this.occurredAt
        );
    }
}
