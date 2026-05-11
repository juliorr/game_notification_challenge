package com.globalli.notifications.events;

public sealed interface SocialEvent permits FriendRequestSent, FriendRequestAccepted, NewFollower {

  long recipientUserId();

  long actorUserId();
}
