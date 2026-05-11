package com.globalli.notifications.events;

public record PlayerDefeatedInPvp(long userId, long attackerUserId) implements GameEvent {}
