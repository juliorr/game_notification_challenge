package com.globalli.notifications.service;

import com.globalli.notifications.channel.ReadStatusChannel;
import com.globalli.notifications.events.AllNotificationsRead;
import com.globalli.notifications.events.NotificationRead;
import com.globalli.notifications.events.NotificationsCleared;
import com.globalli.notifications.events.UserActionEvent;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserActionHandler {

  private static final Logger log = LoggerFactory.getLogger(UserActionHandler.class);

  private final NotificationStore notificationStore;
  private final ReadStatusChannel readStatusChannel;

  public UserActionHandler(
      NotificationStore notificationStore, ReadStatusChannel readStatusChannel) {
    this.notificationStore = notificationStore;
    this.readStatusChannel = readStatusChannel;
  }

  public void handle(UserActionEvent event) {
    switch (event) {
      case NotificationRead read -> handleRead(read);
      case AllNotificationsRead all -> handleAllRead(all);
      case NotificationsCleared cleared -> handleCleared(cleared);
    }
  }

  private void handleRead(NotificationRead event) {
    boolean updated = notificationStore.markRead(event.userId(), event.notificationId());
    if (!updated) {
      log.debug(
          "NotificationRead no-op: user={} id={} (not found or already read)",
          event.userId(),
          event.notificationId());
      return;
    }
    readStatusChannel.broadcastSingle(event.userId(), event.notificationId(), Instant.now());
  }

  private void handleAllRead(AllNotificationsRead event) {
    int updated = notificationStore.markAllRead(event.userId());
    if (updated == 0) {
      log.debug("AllNotificationsRead no-op: user={} (nothing unread)", event.userId());
      return;
    }
    readStatusChannel.broadcastAll(event.userId(), Instant.now());
  }

  private void handleCleared(NotificationsCleared event) {
    int cleared = notificationStore.clearAll(event.userId());
    if (cleared == 0) {
      log.debug("NotificationsCleared no-op: user={} (nothing to clear)", event.userId());
      return;
    }
    readStatusChannel.broadcastCleared(event.userId(), Instant.now());
  }
}
