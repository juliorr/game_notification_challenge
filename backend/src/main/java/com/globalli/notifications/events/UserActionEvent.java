package com.globalli.notifications.events;

public sealed interface UserActionEvent
    permits NotificationRead, AllNotificationsRead, NotificationsCleared {

  long userId();
}
