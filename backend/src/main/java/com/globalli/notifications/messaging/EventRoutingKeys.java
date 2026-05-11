package com.globalli.notifications.messaging;

import com.globalli.notifications.events.AllNotificationsRead;
import com.globalli.notifications.events.ChallengeCompleted;
import com.globalli.notifications.events.FriendRequestAccepted;
import com.globalli.notifications.events.FriendRequestSent;
import com.globalli.notifications.events.GameEvent;
import com.globalli.notifications.events.ItemAcquired;
import com.globalli.notifications.events.NewFollower;
import com.globalli.notifications.events.NotificationRead;
import com.globalli.notifications.events.NotificationsCleared;
import com.globalli.notifications.events.PlayerAttackedInPvp;
import com.globalli.notifications.events.PlayerDefeatedInPvp;
import com.globalli.notifications.events.PlayerLeveledUp;
import com.globalli.notifications.events.SocialEvent;
import com.globalli.notifications.events.UserActionEvent;

public final class EventRoutingKeys {

  public static final String EVENTS_EXCHANGE = "notifications.events";
  public static final String DLX_EXCHANGE = "notifications.events.dlx";

  public static final String GAME_QUEUE = "notifications.dispatch.game.q";
  public static final String SOCIAL_QUEUE = "notifications.dispatch.social.q";
  public static final String USER_QUEUE = "notifications.dispatch.user.q";
  public static final String DEAD_LETTER_QUEUE = "notifications.dispatch.dlq";

  public static final String GAME_BINDING = "game.#";
  public static final String SOCIAL_BINDING = "social.#";
  public static final String USER_BINDING = "user.#";
  public static final String DEAD_LETTER_BINDING = "#";

  public static final String GAME_PLAYER_LEVELED_UP = "game.player.leveled-up";
  public static final String GAME_PLAYER_DEFEATED_PVP = "game.player.defeated-pvp";
  public static final String GAME_PLAYER_ATTACKED_PVP = "game.player.attacked-pvp";
  public static final String GAME_ITEM_ACQUIRED = "game.item.acquired";
  public static final String GAME_CHALLENGE_COMPLETED = "game.challenge.completed";

  public static final String SOCIAL_FRIEND_REQUEST_SENT = "social.friend.request-sent";
  public static final String SOCIAL_FRIEND_REQUEST_ACCEPTED = "social.friend.request-accepted";
  public static final String SOCIAL_FOLLOWER_NEW = "social.follower.new";

  public static final String USER_NOTIFICATION_READ = "user.notification.read";
  public static final String USER_NOTIFICATIONS_ALL_READ = "user.notifications.all-read";
  public static final String USER_NOTIFICATIONS_CLEARED = "user.notifications.cleared";

  private EventRoutingKeys() {}

  public static String routingKeyFor(GameEvent event) {
    return switch (event) {
      case PlayerLeveledUp ignored -> GAME_PLAYER_LEVELED_UP;
      case PlayerDefeatedInPvp ignored -> GAME_PLAYER_DEFEATED_PVP;
      case PlayerAttackedInPvp ignored -> GAME_PLAYER_ATTACKED_PVP;
      case ItemAcquired ignored -> GAME_ITEM_ACQUIRED;
      case ChallengeCompleted ignored -> GAME_CHALLENGE_COMPLETED;
    };
  }

  public static String routingKeyFor(SocialEvent event) {
    return switch (event) {
      case FriendRequestSent ignored -> SOCIAL_FRIEND_REQUEST_SENT;
      case FriendRequestAccepted ignored -> SOCIAL_FRIEND_REQUEST_ACCEPTED;
      case NewFollower ignored -> SOCIAL_FOLLOWER_NEW;
    };
  }

  public static String routingKeyFor(UserActionEvent event) {
    return switch (event) {
      case NotificationRead ignored -> USER_NOTIFICATION_READ;
      case AllNotificationsRead ignored -> USER_NOTIFICATIONS_ALL_READ;
      case NotificationsCleared ignored -> USER_NOTIFICATIONS_CLEARED;
    };
  }
}
