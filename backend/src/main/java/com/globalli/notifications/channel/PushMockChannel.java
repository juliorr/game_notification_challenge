package com.globalli.notifications.channel;

import com.globalli.notifications.domain.DeliveryChannel;
import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.domain.NotificationType;
import com.globalli.notifications.push.MockPushNotification;
import com.globalli.notifications.service.NotificationTemplateService;
import com.globalli.notifications.support.BoundedInMemoryStore;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PushMockChannel extends AbstractFilteredChannel {

  private static final Set<NotificationType> PUSH_ENABLED_TYPES =
      EnumSet.of(
          NotificationType.FRIEND_REQUEST,
          NotificationType.FRIEND_ACCEPTED,
          NotificationType.NEW_FOLLOWER);

  private final BoundedInMemoryStore<MockPushNotification> mockPushStore;
  private final NotificationTemplateService templates;

  public PushMockChannel(
      BoundedInMemoryStore<MockPushNotification> mockPushStore,
      NotificationTemplateService templates) {
    this.mockPushStore = mockPushStore;
    this.templates = templates;
  }

  @Override
  public DeliveryChannel channel() {
    return DeliveryChannel.PUSH;
  }

  @Override
  protected Set<NotificationType> enabledTypes() {
    return PUSH_ENABLED_TYPES;
  }

  @Override
  protected String titleFor(NotificationType type) {
    return templates.pushTitle(type);
  }

  @Override
  protected void deliver(Notification notification, String title) {
    MockPushNotification push =
        new MockPushNotification(
            UUID.randomUUID(),
            notification.userId(),
            deviceToken(notification.userId()),
            title,
            notification.message(),
            Instant.now(),
            notification.id());
    mockPushStore.record(push);
  }

  private static String deviceToken(long userId) {
    return "device-" + userId;
  }
}
