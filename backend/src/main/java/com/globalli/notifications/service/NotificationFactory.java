package com.globalli.notifications.service;

import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.domain.NotificationType;
import com.globalli.notifications.events.ChallengeCompleted;
import com.globalli.notifications.events.FriendRequestAccepted;
import com.globalli.notifications.events.FriendRequestSent;
import com.globalli.notifications.events.GameEvent;
import com.globalli.notifications.events.ItemAcquired;
import com.globalli.notifications.events.NewFollower;
import com.globalli.notifications.events.PlayerAttackedInPvp;
import com.globalli.notifications.events.PlayerDefeatedInPvp;
import com.globalli.notifications.events.PlayerLeveledUp;
import com.globalli.notifications.events.SocialEvent;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {

  private final NotificationTemplateService templates;

  public NotificationFactory(NotificationTemplateService templates) {
    this.templates = templates;
  }

  public Notification fromGameEvent(GameEvent event) {
    return switch (event) {
      case PlayerLeveledUp e ->
          create(e.userId(), NotificationType.LEVEL_UP, new TemplateContext.LevelUp(e.newLevel()));
      case ItemAcquired e ->
          create(
              e.userId(),
              NotificationType.ITEM_ACQUIRED,
              new TemplateContext.ItemAcquired(e.itemName()));
      case ChallengeCompleted e ->
          create(
              e.userId(),
              NotificationType.CHALLENGE_COMPLETED,
              new TemplateContext.ChallengeCompleted(e.challengeName()));
      case PlayerDefeatedInPvp e ->
          create(
              e.userId(),
              NotificationType.PVP_DEFEATED,
              new TemplateContext.PvpDefeated(e.attackerUserId()));
      case PlayerAttackedInPvp e ->
          create(
              e.userId(),
              NotificationType.PVP_ATTACKED,
              new TemplateContext.PvpAttacked(e.attackerUserId()));
    };
  }

  public Notification fromSocialEvent(SocialEvent event) {
    return switch (event) {
      case FriendRequestSent e ->
          create(
              e.recipientUserId(),
              NotificationType.FRIEND_REQUEST,
              new TemplateContext.SocialActor(e.actorUserId()));
      case FriendRequestAccepted e ->
          create(
              e.recipientUserId(),
              NotificationType.FRIEND_ACCEPTED,
              new TemplateContext.SocialActor(e.actorUserId()));
      case NewFollower e ->
          create(
              e.recipientUserId(),
              NotificationType.NEW_FOLLOWER,
              new TemplateContext.SocialActor(e.actorUserId()));
    };
  }

  private Notification create(long userId, NotificationType type, TemplateContext context) {
    return Notification.create(userId, type, templates.render(type, context));
  }
}
