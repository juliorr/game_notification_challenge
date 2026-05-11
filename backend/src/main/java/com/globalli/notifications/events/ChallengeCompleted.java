package com.globalli.notifications.events;

public record ChallengeCompleted(long userId, String challengeName) implements GameEvent {}
