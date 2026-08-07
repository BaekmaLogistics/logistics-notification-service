package com.sparta.logistics.notification.infrastructure.persistence.command.repository;

import com.sparta.logistics.notification.infrastructure.persistence.command.entity.SlackMessageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface SlackMessageCommandJpaRepository extends JpaRepository<SlackMessageJpaEntity, UUID> {
}
