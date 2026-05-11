package com.globalli.notifications.service;

import com.globalli.notifications.config.NotificationsProperties;
import com.globalli.notifications.domain.Notification;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class NotificationQueryService {

  private final NotificationStore notificationStore;
  private final NotificationsProperties.Pagination pagination;

  public NotificationQueryService(
      NotificationStore notificationStore, NotificationsProperties properties) {
    this.notificationStore = notificationStore;
    this.pagination = properties.pagination();
  }

  public List<Notification> findPage(
      long userId, Optional<Instant> cursor, Integer requestedLimit) {
    int limit =
        Math.min(
            pagination.maxPageSize(),
            requestedLimit != null ? requestedLimit : pagination.defaultPageSize());
    return notificationStore.findPage(userId, cursor, limit);
  }

  public long unreadCount(long userId) {
    return notificationStore.unreadCount(userId);
  }
}
