package com.globalli.notifications.service;

import com.globalli.notifications.channel.NotificationChannel;
import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.events.GameEvent;
import com.globalli.notifications.events.SocialEvent;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationDispatcher {

  private static final Logger log = LoggerFactory.getLogger(NotificationDispatcher.class);

  private final NotificationFactory notificationFactory;
  private final UserPreferenceService userPreferenceService;
  private final NotificationStore notificationStore;
  private final List<NotificationChannel> channels;

  public NotificationDispatcher(
      NotificationFactory notificationFactory,
      UserPreferenceService userPreferenceService,
      NotificationStore notificationStore,
      List<NotificationChannel> channels) {
    this.notificationFactory = notificationFactory;
    this.userPreferenceService = userPreferenceService;
    this.notificationStore = notificationStore;
    this.channels = channels;
  }

  public void handle(GameEvent event) {
    dispatch(notificationFactory.fromGameEvent(event));
  }

  public void handle(SocialEvent event) {
    dispatch(notificationFactory.fromSocialEvent(event));
  }

  private void dispatch(Notification notification) {
    if (!isUserAllowedToReceive(notification)) {
      return;
    }
    notificationStore.append(notification);
    deliverThroughChannels(notification);
  }

  private boolean isUserAllowedToReceive(Notification notification) {
    long userId = notification.userId();
    if (!userPreferenceService.isNotificationsEnabled(userId)) {
      log.debug(
          "Skipping notification {} for user {}: notifications are disabled",
          notification.type(),
          userId);
      return false;
    }
    if (!userPreferenceService.isCategoryEnabled(userId, notification.category())) {
      log.debug(
          "Skipping notification {} for user {}: category {} is disabled",
          notification.type(),
          userId,
          notification.category());
      return false;
    }
    return true;
  }

  private void deliverThroughChannels(Notification notification) {
    long userId = notification.userId();
    for (NotificationChannel channel : channels) {
      if (!isChannelEnabled(userId, channel)) {
        continue;
      }
      channel.send(notification);
    }
  }

  private boolean isChannelEnabled(long userId, NotificationChannel channel) {
    if (userPreferenceService.isChannelEnabled(userId, channel.channel())) {
      return true;
    }
    log.debug("Skipping channel {} for user {}: channel is disabled", channel.channel(), userId);
    return false;
  }
}
