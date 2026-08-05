package com.sparta.logistics.notification.infrastructure.persistence.command.repository;

import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.domain.repository.SlackMessageCommandRepository;
import com.sparta.logistics.notification.infrastructure.persistence.command.entity.SlackMessageJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SlackMessageCommandRepositoryImpl implements SlackMessageCommandRepository {
    private final SlackMessageCommandJpaRepository commandJpaRepository;

    @Override
    public void append(SlackMessage slackMessage) {
        SlackMessageJpaEntity jpaEntity = SlackMessageJpaEntity.createFromModel(slackMessage);

        commandJpaRepository.save(jpaEntity);
    }
}
