package com.globalli.notifications.domain.persistence;

import com.globalli.notifications.domain.NotificationCategory;
import com.globalli.notifications.domain.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private long userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 64)
  private NotificationType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 64)
  private NotificationCategory category;

  @Column(nullable = false)
  private String message;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "read_at")
  private Instant readAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  protected NotificationEntity() {}

  public NotificationEntity(
      UUID id,
      long userId,
      NotificationType type,
      NotificationCategory category,
      String message,
      Instant createdAt,
      Instant readAt) {
    this.id = id;
    this.userId = userId;
    this.type = type;
    this.category = category;
    this.message = message;
    this.createdAt = createdAt;
    this.readAt = readAt;
  }

  public UUID getId() {
    return id;
  }

  public long getUserId() {
    return userId;
  }

  public NotificationType getType() {
    return type;
  }

  public NotificationCategory getCategory() {
    return category;
  }

  public String getMessage() {
    return message;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getReadAt() {
    return readAt;
  }

  public Instant getDeletedAt() {
    return deletedAt;
  }
}
