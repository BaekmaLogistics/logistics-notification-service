package com.sparta.logistics.notification.infrastructure.persistence.command.repository;

import com.sparta.logistics.notification.domain.model.SlackMessageStatus;
import com.sparta.logistics.notification.infrastructure.persistence.command.entity.SlackMessageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
interface SlackMessageCommandJpaRepository extends JpaRepository<SlackMessageJpaEntity, UUID> {
    List<SlackMessageJpaEntity> findAllByStatusInAndDeletedAtIsNull(Collection<SlackMessageStatus> statuses);
}
