package com.sparta.logistics.notification.infrastructure.persistence.command.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSlackMessageJpaEntity is a Querydsl query type for SlackMessageJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSlackMessageJpaEntity extends EntityPathBase<SlackMessageJpaEntity> {

    private static final long serialVersionUID = 518492695L;

    public static final QSlackMessageJpaEntity slackMessageJpaEntity = new QSlackMessageJpaEntity("slackMessageJpaEntity");

    public final com.sparta.logistics.notification.infrastructure.persistence.common.entity.QBaseUpdatableJpaEntity _super = new com.sparta.logistics.notification.infrastructure.persistence.common.entity.QBaseUpdatableJpaEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    //inherited
    public final ComparablePath<java.util.UUID> createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.Instant> deletedAt = _super.deletedAt;

    //inherited
    public final ComparablePath<java.util.UUID> deletedBy = _super.deletedBy;

    public final StringPath errorMessage = createString("errorMessage");

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public final ComparablePath<java.util.UUID> receiverId = createComparable("receiverId", java.util.UUID.class);

    public final StringPath receiverSlackId = createString("receiverSlackId");

    public final NumberPath<Integer> retryCount = createNumber("retryCount", Integer.class);

    public final ComparablePath<java.util.UUID> senderId = createComparable("senderId", java.util.UUID.class);

    public final StringPath senderSlackId = createString("senderSlackId");

    public final EnumPath<com.sparta.logistics.notification.domain.model.SlackMessageStatus> status = createEnum("status", com.sparta.logistics.notification.domain.model.SlackMessageStatus.class);

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    //inherited
    public final ComparablePath<java.util.UUID> updatedBy = _super.updatedBy;

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QSlackMessageJpaEntity(String variable) {
        super(SlackMessageJpaEntity.class, forVariable(variable));
    }

    public QSlackMessageJpaEntity(Path<? extends SlackMessageJpaEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSlackMessageJpaEntity(PathMetadata metadata) {
        super(SlackMessageJpaEntity.class, metadata);
    }

}

