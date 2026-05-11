package com.globalli.notifications.channel;

import static org.assertj.core.api.Assertions.assertThat;

import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.domain.NotificationType;
import com.globalli.notifications.email.MockEmail;
import com.globalli.notifications.email.MockEmailStore;
import com.globalli.notifications.service.NotificationTemplateService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmailMockChannelTest {

  private MockEmailStore store;
  private EmailMockChannel channel;

  @BeforeEach
  void setUp() {
    store = new MockEmailStore();
    channel = new EmailMockChannel(store, new NotificationTemplateService());
  }

  @Test
  void recordsEmailForFriendRequest() {
    Notification notification =
        Notification.create(
            42L, NotificationType.FRIEND_REQUEST, "User 7 sent you a friend request");

    channel.send(notification);

    List<MockEmail> emails = store.findByUserId(42L);
    assertThat(emails).hasSize(1);
    MockEmail email = emails.get(0);
    assertThat(email.subject()).isEqualTo("You have a new friend request");
    assertThat(email.body()).isEqualTo("User 7 sent you a friend request");
    assertThat(email.to()).isEqualTo("user-42@globalli.local");
    assertThat(email.sourceNotificationId()).isEqualTo(notification.id());
  }

  @Test
  void recordsEmailForFriendAccepted() {
    Notification notification =
        Notification.create(9L, NotificationType.FRIEND_ACCEPTED, "User 3 accepted your request");

    channel.send(notification);

    List<MockEmail> emails = store.findAll();
    assertThat(emails).hasSize(1);
    assertThat(emails.get(0).subject()).isEqualTo("Your friend request was accepted");
  }

  @Test
  void ignoresOtherTypes() {
    channel.send(Notification.create(1L, NotificationType.LEVEL_UP, "Level 2!"));
    channel.send(Notification.create(1L, NotificationType.ITEM_ACQUIRED, "Sword"));
    channel.send(Notification.create(1L, NotificationType.CHALLENGE_COMPLETED, "Daily"));
    channel.send(Notification.create(1L, NotificationType.PVP_DEFEATED, "By 2"));
    channel.send(Notification.create(1L, NotificationType.NEW_FOLLOWER, "User 5"));

    assertThat(store.findAll()).isEmpty();
  }
}
