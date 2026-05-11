package com.globalli.notifications.events;

public record NotificationsCleared(long userId) implements UserActionEvent {}
