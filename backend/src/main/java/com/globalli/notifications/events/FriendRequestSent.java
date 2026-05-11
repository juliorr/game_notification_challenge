package com.globalli.notifications.events;

public record FriendRequestSent(long actorUserId, long recipientUserId) implements SocialEvent {}
