package com.globalli.notifications.events;

public record PlayerLeveledUp(long userId, int newLevel) implements GameEvent {}
