package com.globalli.notifications.channel;

import static org.assertj.core.api.Assertions.assertThat;

import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.domain.NotificationType;
import com.globalli.notifications.push.MockPushNotification;
import com.globalli.notifications.service.NotificationTemplateService;
import com.globalli.notifications.support.BoundedInMemoryStore;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PushMockChannelTest {

  private BoundedInMemoryStore<MockPushNotification> store;
  private PushMockChannel channel;

  @BeforeEach
  void setUp() {
    store = new BoundedInMemoryStore<>(MockPushNotification::userId);
    channel = new PushMockChannel(store, new NotificationTemplateService());
  }

  @Test
  void recordsPushForFriendRequest() {
    Notification notification =
        Notification.create(
            42L, NotificationType.FRIEND_REQUEST, "User 7 sent you a friend request");

    channel.send(notification);

    List<MockPushNotification> entries = store.findByUserId(42L);
    assertThat(entries).hasSize(1);
    MockPushNotification push = entries.get(0);
    assertThat(push.title()).isEqualTo("New friend request");
    assertThat(push.body()).isEqualTo("User 7 sent you a friend request");
    assertThat(push.deviceToken()).isEqualTo("device-42");
    assertThat(push.sourceNotificationId()).isEqualTo(notification.id());
  }

  @Test
  void recordsPushForFriendAccepted() {
    channel.send(Notification.create(9L, NotificationType.FRIEND_ACCEPTED, "User 3 accepted"));

    assertThat(store.findAll()).hasSize(1);
    assertThat(store.findAll().get(0).title()).isEqualTo("Friend request accepted");
  }

  @Test
  void recordsPushForNewFollower() {
    channel.send(
        Notification.create(5L, NotificationType.NEW_FOLLOWER, "User 12 started following"));

    assertThat(store.findAll()).hasSize(1);
    assertThat(store.findAll().get(0).title()).isEqualTo("You have a new follower");
  }

  @Test
  void ignoresGameTypes() {
    channel.send(Notification.create(1L, NotificationType.LEVEL_UP, "Level 2!"));
    channel.send(Notification.create(1L, NotificationType.ITEM_ACQUIRED, "Sword"));
    channel.send(Notification.create(1L, NotificationType.CHALLENGE_COMPLETED, "Daily"));
    channel.send(Notification.create(1L, NotificationType.PVP_DEFEATED, "By 2"));

    assertThat(store.findAll()).isEmpty();
  }
}
