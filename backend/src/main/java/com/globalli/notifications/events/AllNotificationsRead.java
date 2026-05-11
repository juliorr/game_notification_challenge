package com.globalli.notifications.events;

public record AllNotificationsRead(long userId) implements UserActionEvent {}
