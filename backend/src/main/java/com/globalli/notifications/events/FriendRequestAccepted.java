package com.globalli.notifications.events;

public record FriendRequestAccepted(long actorUserId, long recipientUserId)
    implements SocialEvent {}
