package com.globalli.notifications.events;

public record NewFollower(long actorUserId, long recipientUserId) implements SocialEvent {}
