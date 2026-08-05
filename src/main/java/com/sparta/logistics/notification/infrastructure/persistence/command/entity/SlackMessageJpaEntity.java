package com.sparta.logistics.notification.infrastructure.persistence.command.entity;

import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.domain.model.AuditInfo;
import com.sparta.logistics.notification.domain.model.DeletionInfo;
import com.sparta.logistics.notification.domain.model.SlackMessageStatus;
import com.sparta.logistics.notification.infrastructure.persistence.common.entity.BaseUpdatableJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_slack_messages")
public class SlackMessageJpaEntity extends BaseUpdatableJpaEntity {
    @Column(name = "receiver_id", nullable = false, updatable = false)
    private UUID receiverId;

    @Column(name = "sender_id", nullable = false, updatable = false)
    private UUID senderId;

    @Column(name = "content", nullable = false, columnDefinition = "VARCHAR(1024)")
    private String content;

    @ColumnDefault(value = "'PENDING'")
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(24)")
    @Enumerated(EnumType.STRING)
    private SlackMessageStatus status;

    @ColumnDefault(value = "0")
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Column(name = "error_message", columnDefinition = "VARCHAR(128)")
    private String errorMessage;

    @Version
    @Column(name = "version")
    private Long version;

    public static SlackMessageJpaEntity createFromModel(
            SlackMessage slackMessage
    ) {
        return SlackMessageJpaEntity.builder()
                .receiverId(slackMessage.receiverId())
                .senderId(slackMessage.senderId())
                .content(slackMessage.content())
                .status(SlackMessageStatus.PENDING)
                .retryCount(0)
                .build();
    }

    public void updateFromModel(SlackMessage slackMessage) {
        this.content = slackMessage.content();
        this.status = slackMessage.status();
        this.retryCount = slackMessage.retryCount();
        this.errorMessage = slackMessage.errorMessage();
    }

    public SlackMessage toModel() {
        return new SlackMessage(
                getId(),
                receiverId,
                senderId,
                content,
                status,
                retryCount,
                errorMessage,
                new AuditInfo(
                        getCreatedAt(), getCreatedBy(), getUpdatedAt(), getUpdatedBy()
                ),
                new DeletionInfo(
                        getDeletedAt(), getDeletedBy()
                )
        );
    }

    @Builder
    private SlackMessageJpaEntity(
            UUID receiverId,
            UUID senderId,
            String content,
            SlackMessageStatus status,
            int retryCount
    ) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.content = content;
        this.status = status;
        this.retryCount = retryCount;
    }
}
