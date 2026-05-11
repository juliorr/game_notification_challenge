package com.globalli.notifications.simulators;

import static org.mockito.Mockito.verify;

import com.globalli.notifications.events.FriendRequestAccepted;
import com.globalli.notifications.events.FriendRequestSent;
import com.globalli.notifications.events.NewFollower;
import com.globalli.notifications.messaging.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SocialSystemTest {

  @Mock private DomainEventPublisher publisher;

  private SocialSystem system;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    system = new SocialSystem(publisher);
  }

  @Test
  void publishesFriendRequestSent() {
    system.friendRequestSent(1L, 2L);

    verify(publisher).publish(new FriendRequestSent(1L, 2L));
  }

  @Test
  void publishesFriendRequestAccepted() {
    system.friendRequestAccepted(2L, 1L);

    verify(publisher).publish(new FriendRequestAccepted(2L, 1L));
  }

  @Test
  void publishesNewFollower() {
    system.newFollower(3L, 5L);

    verify(publisher).publish(new NewFollower(3L, 5L));
  }
}
