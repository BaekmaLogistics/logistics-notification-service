package com.sparta.logistics.notification.infrastructure.persistence.common.entity;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
public abstract class BaseUpdatableJpaEntity extends BaseJpaEntity {

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    public void updateAudit(UUID updatedBy) {
        this.updatedAt = Instant.now();
        if (updatedBy != null) {
            this.updatedBy = updatedBy;
        }
    }

    public void softDelete(UUID deletedBy) {
        this.deletedAt = Instant.now();
        this.deletedBy = deletedBy;
    }
}