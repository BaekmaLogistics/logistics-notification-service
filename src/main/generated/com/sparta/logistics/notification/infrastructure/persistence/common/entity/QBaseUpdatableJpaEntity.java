package com.sparta.logistics.notification.infrastructure.persistence.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseUpdatableJpaEntity is a Querydsl query type for BaseUpdatableJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseUpdatableJpaEntity extends EntityPathBase<BaseUpdatableJpaEntity> {

    private static final long serialVersionUID = 1635800953L;

    public static final QBaseUpdatableJpaEntity baseUpdatableJpaEntity = new QBaseUpdatableJpaEntity("baseUpdatableJpaEntity");

    public final QBaseJpaEntity _super = new QBaseJpaEntity(this);

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    //inherited
    public final ComparablePath<java.util.UUID> createdBy = _super.createdBy;

    public final DateTimePath<java.time.Instant> deletedAt = createDateTime("deletedAt", java.time.Instant.class);

    public final ComparablePath<java.util.UUID> deletedBy = createComparable("deletedBy", java.util.UUID.class);

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public final DateTimePath<java.time.Instant> updatedAt = createDateTime("updatedAt", java.time.Instant.class);

    public final ComparablePath<java.util.UUID> updatedBy = createComparable("updatedBy", java.util.UUID.class);

    public QBaseUpdatableJpaEntity(String variable) {
        super(BaseUpdatableJpaEntity.class, forVariable(variable));
    }

    public QBaseUpdatableJpaEntity(Path<? extends BaseUpdatableJpaEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseUpdatableJpaEntity(PathMetadata metadata) {
        super(BaseUpdatableJpaEntity.class, metadata);
    }

}

