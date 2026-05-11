package com.globalli.notifications.events;

public record PlayerAttackedInPvp(long userId, long attackerUserId) implements GameEvent {}
