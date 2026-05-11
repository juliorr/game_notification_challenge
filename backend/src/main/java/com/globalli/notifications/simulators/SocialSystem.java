package com.globalli.notifications.simulators;

import com.globalli.notifications.events.FriendRequestAccepted;
import com.globalli.notifications.events.FriendRequestSent;
import com.globalli.notifications.events.NewFollower;
import com.globalli.notifications.messaging.DomainEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SocialSystem {

  private final DomainEventPublisher eventPublisher;

  public SocialSystem(DomainEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public void friendRequestSent(long fromUserId, long toUserId) {
    eventPublisher.publish(new FriendRequestSent(fromUserId, toUserId));
  }

  public void friendRequestAccepted(long acceptingUserId, long requesterUserId) {
    eventPublisher.publish(new FriendRequestAccepted(acceptingUserId, requesterUserId));
  }

  public void newFollower(long followerUserId, long followedUserId) {
    eventPublisher.publish(new NewFollower(followerUserId, followedUserId));
  }
}
