package com.globalli.notifications.events;

import java.util.UUID;

public record NotificationRead(long userId, UUID notificationId) implements UserActionEvent {}
