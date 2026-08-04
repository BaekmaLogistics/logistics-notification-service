package com.sparta.notification.infrastructure.persistence.jpa.repository;

import com.sparta.notification.infrastructure.persistence.jpa.entity.SlackMessageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SlackMessageJpaRepository extends JpaRepository<SlackMessageJpaEntity, UUID> {
}
