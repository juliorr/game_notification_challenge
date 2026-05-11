package com.globalli.notifications.domain.persistence;

import com.globalli.notifications.domain.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationEntityMapper {

  public NotificationEntity toEntity(Notification notification) {
    return new NotificationEntity(
        notification.id(),
        notification.userId(),
        notification.type(),
        notification.category(),
        notification.message(),
        notification.timestamp(),
        notification.readAt());
  }

  public Notification toDomain(NotificationEntity entity) {
    return new Notification(
        entity.getId(),
        entity.getUserId(),
        entity.getType(),
        entity.getCategory(),
        entity.getMessage(),
        entity.getCreatedAt(),
        entity.getReadAt());
  }
}
