package com.globalli.notifications.service;

import com.globalli.notifications.domain.Notification;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationStore {

  int DEFAULT_PAGE_SIZE = 20;

  void append(Notification notification);

  List<Notification> findByUserId(long userId);

  List<Notification> findPage(long userId, Optional<Instant> cursor, int limit);

  long unreadCount(long userId);

  int markAllRead(long userId);

  boolean markRead(long userId, UUID notificationId);

  int clearAll(long userId);
}
