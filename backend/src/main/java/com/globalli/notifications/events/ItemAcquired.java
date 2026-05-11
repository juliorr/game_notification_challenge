package com.globalli.notifications.events;

public record ItemAcquired(long userId, String itemName) implements GameEvent {}
