package com.globalli.notifications.domain;

public enum NotificationType {
  LEVEL_UP(NotificationCategory.GAME),
  ITEM_ACQUIRED(NotificationCategory.GAME),
  CHALLENGE_COMPLETED(NotificationCategory.GAME),
  PVP_DEFEATED(NotificationCategory.GAME),
  PVP_ATTACKED(NotificationCategory.GAME),
  FRIEND_REQUEST(NotificationCategory.SOCIAL),
  FRIEND_ACCEPTED(NotificationCategory.SOCIAL),
  NEW_FOLLOWER(NotificationCategory.SOCIAL);

  private final NotificationCategory category;

  NotificationType(NotificationCategory category) {
    this.category = category;
  }

  public NotificationCategory category() {
    return category;
  }
}
