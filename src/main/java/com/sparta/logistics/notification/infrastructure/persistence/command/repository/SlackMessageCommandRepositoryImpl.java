package com.sparta.logistics.notification.infrastructure.persistence.command.repository;

import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.domain.model.SlackMessageStatus;
import com.sparta.logistics.notification.domain.repository.SlackMessageCommandRepository;
import com.sparta.logistics.notification.infrastructure.persistence.command.entity.SlackMessageJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SlackMessageCommandRepositoryImpl implements SlackMessageCommandRepository {
    private final SlackMessageCommandJpaRepository commandJpaRepository;

    @Override
    public SlackMessage append(SlackMessage slackMessage) {
        SlackMessageJpaEntity jpaEntity = SlackMessageJpaEntity.createFromModel(slackMessage);
        return commandJpaRepository.save(jpaEntity).toModel();
    }

    @Override
    public Optional<SlackMessage> findById(UUID slackMessageId) {
        return commandJpaRepository.findById(slackMessageId)
                .filter(entity -> entity.getDeletedAt() == null)
                .map(SlackMessageJpaEntity::toModel);
    }

    @Override
    public List<SlackMessage> findAllByStatusIn(List<SlackMessageStatus> statuses) {
        return commandJpaRepository.findAllByStatusInAndDeletedAtIsNull(statuses)
                .stream()
                .map(SlackMessageJpaEntity::toModel)
                .toList();
    }

    @Override
    public void update(SlackMessage slackMessage) {
        SlackMessageJpaEntity jpaEntity = commandJpaRepository.findById(slackMessage.id())
                .filter(entity -> entity.getDeletedAt() == null)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.SLACK_MESSAGE_NOT_FOUND));

        jpaEntity.updateFromModel(slackMessage);
    }

    @Override
    public void delete(UUID slackMessageId, UUID deletedBy) {
        SlackMessageJpaEntity jpaEntity = commandJpaRepository.findById(slackMessageId)
                .filter(entity -> entity.getDeletedAt() == null)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.SLACK_MESSAGE_NOT_FOUND));

        jpaEntity.softDelete(deletedBy);
    }
}
