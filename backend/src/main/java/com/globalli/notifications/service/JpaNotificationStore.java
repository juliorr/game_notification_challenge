package com.globalli.notifications.service;

import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.domain.persistence.NotificationEntity;
import com.globalli.notifications.domain.persistence.NotificationEntityMapper;
import com.globalli.notifications.domain.persistence.NotificationJpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Primary
public class JpaNotificationStore implements NotificationStore {

  private final NotificationJpaRepository repository;
  private final NotificationEntityMapper mapper;

  public JpaNotificationStore(
      NotificationJpaRepository repository, NotificationEntityMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public void append(Notification notification) {
    repository.save(mapper.toEntity(notification));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Notification> findByUserId(long userId) {
    return findPage(userId, Optional.empty(), DEFAULT_PAGE_SIZE);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Notification> findPage(long userId, Optional<Instant> cursor, int limit) {
    PageRequest page = PageRequest.of(0, limit);
    List<NotificationEntity> rows =
        cursor
            .map(c -> repository.findActiveByUserIdBefore(userId, c, page))
            .orElseGet(() -> repository.findActiveByUserId(userId, page));
    return rows.stream().map(mapper::toDomain).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public long unreadCount(long userId) {
    return repository.countUnread(userId);
  }

  @Override
  @Transactional
  public int markAllRead(long userId) {
    return repository.markAllRead(userId, Instant.now());
  }

  @Override
  @Transactional
  public boolean markRead(long userId, UUID notificationId) {
    return repository.markRead(userId, notificationId, Instant.now()) > 0;
  }

  @Override
  @Transactional
  public int clearAll(long userId) {
    return repository.softDeleteAllByUserId(userId, Instant.now());
  }
}
