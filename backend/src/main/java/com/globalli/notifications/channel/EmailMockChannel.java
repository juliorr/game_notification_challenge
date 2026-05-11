package com.globalli.notifications.channel;

import com.globalli.notifications.domain.DeliveryChannel;
import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.domain.NotificationType;
import com.globalli.notifications.email.MockEmail;
import com.globalli.notifications.email.MockEmailStore;
import com.globalli.notifications.service.NotificationTemplateService;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class EmailMockChannel extends AbstractFilteredChannel {

  private static final Set<NotificationType> EMAIL_ENABLED_TYPES =
      EnumSet.of(NotificationType.FRIEND_REQUEST, NotificationType.FRIEND_ACCEPTED);

  private final MockEmailStore mockEmailStore;
  private final NotificationTemplateService templates;

  public EmailMockChannel(MockEmailStore mockEmailStore, NotificationTemplateService templates) {
    this.mockEmailStore = mockEmailStore;
    this.templates = templates;
  }

  @Override
  public DeliveryChannel channel() {
    return DeliveryChannel.EMAIL;
  }

  @Override
  protected Set<NotificationType> enabledTypes() {
    return EMAIL_ENABLED_TYPES;
  }

  @Override
  protected String titleFor(NotificationType type) {
    return templates.emailSubject(type);
  }

  @Override
  protected void deliver(Notification notification, String subject) {
    MockEmail email =
        new MockEmail(
            UUID.randomUUID(),
            notification.userId(),
            recipientAddress(notification.userId()),
            subject,
            notification.message(),
            Instant.now(),
            notification.id());
    mockEmailStore.record(email);
  }

  private static String recipientAddress(long userId) {
    return "user-" + userId + "@globalli.local";
  }
}
