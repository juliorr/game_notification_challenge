package com.globalli.notifications.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.domain.NotificationCategory;
import com.globalli.notifications.domain.NotificationType;
import com.globalli.notifications.events.ChallengeCompleted;
import com.globalli.notifications.events.FriendRequestAccepted;
import com.globalli.notifications.events.FriendRequestSent;
import com.globalli.notifications.events.ItemAcquired;
import com.globalli.notifications.events.NewFollower;
import com.globalli.notifications.events.PlayerAttackedInPvp;
import com.globalli.notifications.events.PlayerDefeatedInPvp;
import com.globalli.notifications.events.PlayerLeveledUp;
import org.junit.jupiter.api.Test;

class NotificationFactoryTest {

  private final NotificationFactory factory =
      new NotificationFactory(new NotificationTemplateService());

  @Test
  void buildsLevelUpNotification() {
    Notification result = factory.fromGameEvent(new PlayerLeveledUp(1L, 15));

    assertThat(result.userId()).isEqualTo(1L);
    assertThat(result.type()).isEqualTo(NotificationType.LEVEL_UP);
    assertThat(result.category()).isEqualTo(NotificationCategory.GAME);
    assertThat(result.message()).isEqualTo("Congratulations! You've reached level 15!");
  }

  @Test
  void buildsItemAcquiredNotification() {
    Notification result = factory.fromGameEvent(new ItemAcquired(2L, "Sword of Azeroth"));

    assertThat(result.type()).isEqualTo(NotificationType.ITEM_ACQUIRED);
    assertThat(result.message()).isEqualTo("You've acquired the legendary Sword of Azeroth!");
  }

  @Test
  void buildsChallengeCompletedNotification() {
    Notification result = factory.fromGameEvent(new ChallengeCompleted(3L, "Dragon Slayer"));

    assertThat(result.type()).isEqualTo(NotificationType.CHALLENGE_COMPLETED);
    assertThat(result.message()).isEqualTo("Challenge completed: Dragon Slayer!");
  }

  @Test
  void buildsPvpDefeatedNotification() {
    Notification result = factory.fromGameEvent(new PlayerDefeatedInPvp(4L, 7L));

    assertThat(result.type()).isEqualTo(NotificationType.PVP_DEFEATED);
    assertThat(result.message()).isEqualTo("You were defeated in PvP by player 7.");
  }

  @Test
  void buildsPvpAttackedNotification() {
    Notification result = factory.fromGameEvent(new PlayerAttackedInPvp(4L, 7L));

    assertThat(result.userId()).isEqualTo(4L);
    assertThat(result.type()).isEqualTo(NotificationType.PVP_ATTACKED);
    assertThat(result.category()).isEqualTo(NotificationCategory.GAME);
    assertThat(result.message()).isEqualTo("You were attacked in PvP by player 7.");
  }

  @Test
  void buildsFriendRequestNotification() {
    Notification result = factory.fromSocialEvent(new FriendRequestSent(3L, 1L));

    assertThat(result.userId()).isEqualTo(1L);
    assertThat(result.type()).isEqualTo(NotificationType.FRIEND_REQUEST);
    assertThat(result.category()).isEqualTo(NotificationCategory.SOCIAL);
    assertThat(result.message()).isEqualTo("Player '3' has sent you a friend request.");
  }

  @Test
  void buildsFriendAcceptedNotification() {
    Notification result = factory.fromSocialEvent(new FriendRequestAccepted(1L, 3L));

    assertThat(result.type()).isEqualTo(NotificationType.FRIEND_ACCEPTED);
    assertThat(result.message()).isEqualTo("Player '1' accepted your friend request.");
  }

  @Test
  void buildsNewFollowerNotification() {
    Notification result = factory.fromSocialEvent(new NewFollower(2L, 1L));

    assertThat(result.type()).isEqualTo(NotificationType.NEW_FOLLOWER);
    assertThat(result.message()).isEqualTo("Player '2' is now following you.");
  }
}
