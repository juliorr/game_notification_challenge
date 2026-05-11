package com.globalli.notifications.channel;

import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.domain.NotificationType;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFilteredChannel implements NotificationChannel {

  private final Logger log = LoggerFactory.getLogger(getClass());

  protected abstract Set<NotificationType> enabledTypes();

  protected abstract String titleFor(NotificationType type);

  protected abstract void deliver(Notification notification, String title);

  @Override
  public final void send(Notification notification) {
    if (!enabledTypes().contains(notification.type())) {
      return;
    }
    String title = titleFor(notification.type());
    deliver(notification, title);
    log.info(
        "Channel {} delivered notification {} to user {}",
        channel(),
        notification.id(),
        notification.userId());
  }
}
