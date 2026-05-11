package com.globalli.notifications.domain;

import java.time.Instant;
import java.util.UUID;

public record Notification(
    UUID id,
    long userId,
    NotificationType type,
    NotificationCategory category,
    String message,
    Instant timestamp,
    Instant readAt) {

  public static Notification create(long userId, NotificationType type, String message) {
    return new Notification(
        UUID.randomUUID(), userId, type, type.category(), message, Instant.now(), null);
  }

  public Notification markRead(Instant when) {
    return new Notification(id, userId, type, category, message, timestamp, when);
  }
}
