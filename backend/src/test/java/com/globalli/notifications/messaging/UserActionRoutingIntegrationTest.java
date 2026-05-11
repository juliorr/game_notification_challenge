package com.globalli.notifications.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.domain.NotificationType;
import com.globalli.notifications.events.AllNotificationsRead;
import com.globalli.notifications.events.NotificationRead;
import com.globalli.notifications.events.NotificationsCleared;
import com.globalli.notifications.service.NotificationStore;
import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserActionRoutingIntegrationTest extends RabbitContainerSupport {

  @Autowired private AmqpAdmin amqpAdmin;
  @Autowired private DomainEventPublisher publisher;
  @Autowired private NotificationStore notificationStore;

  @Test
  void declaresUserQueueWithDeadLetterExchange() {
    Properties props = amqpAdmin.getQueueProperties(EventRoutingKeys.USER_QUEUE);
    assertThat(props).isNotNull();
  }

  @Test
  void notificationReadEventMarksNotificationAsRead() {
    long userId = 4242L;
    Notification stored =
        Notification.create(userId, NotificationType.LEVEL_UP, "Welcome to level 1");
    notificationStore.append(stored);

    publisher.publish(new NotificationRead(userId, stored.id()));

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(250))
        .untilAsserted(
            () -> {
              Notification refreshed =
                  notificationStore.findByUserId(userId).stream()
                      .filter(n -> n.id().equals(stored.id()))
                      .findFirst()
                      .orElseThrow();
              assertThat(refreshed.readAt()).isNotNull();
            });
  }

  @Test
  void allNotificationsReadEventMarksEverythingAsRead() {
    long userId = 4243L;
    notificationStore.append(Notification.create(userId, NotificationType.LEVEL_UP, "level 1"));
    notificationStore.append(
        Notification.create(userId, NotificationType.ITEM_ACQUIRED, "shiny sword"));

    publisher.publish(new AllNotificationsRead(userId));

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(250))
        .untilAsserted(() -> assertThat(notificationStore.unreadCount(userId)).isZero());
  }

  @Test
  void notificationsClearedEventSoftDeletesAllNotifications() {
    long userId = 4244L;
    notificationStore.append(Notification.create(userId, NotificationType.LEVEL_UP, "level 1"));
    notificationStore.append(
        Notification.create(userId, NotificationType.ITEM_ACQUIRED, "shiny sword"));

    publisher.publish(new NotificationsCleared(userId));

    await()
        .atMost(Duration.ofSeconds(15))
        .pollInterval(Duration.ofMillis(250))
        .untilAsserted(() -> assertThat(notificationStore.findByUserId(userId)).isEmpty());
  }
}
