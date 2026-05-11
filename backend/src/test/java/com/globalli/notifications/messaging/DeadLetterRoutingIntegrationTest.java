package com.globalli.notifications.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.globalli.notifications.events.FriendRequestSent;
import com.globalli.notifications.events.NotificationRead;
import com.globalli.notifications.events.PlayerLeveledUp;
import com.globalli.notifications.service.NotificationDispatcher;
import com.globalli.notifications.service.UserActionHandler;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DeadLetterRoutingIntegrationTest extends RabbitContainerSupport {

  @Autowired private AmqpAdmin amqpAdmin;
  @Autowired private DomainEventPublisher publisher;
  @MockBean private NotificationDispatcher dispatcher;
  @MockBean private UserActionHandler userActionHandler;

  @Test
  void gameEventFailureRetriesUntilDeliveryLimitAndLandsInDlq() {
    AtomicInteger attempts = new AtomicInteger();
    Mockito.doAnswer(
            invocation -> {
              attempts.incrementAndGet();
              throw new RuntimeException("forced failure");
            })
        .when(dispatcher)
        .handle(Mockito.any(PlayerLeveledUp.class));

    int previousDlqDepth = currentDlqDepth();

    publisher.publish(new PlayerLeveledUp(101L, 1));

    await()
        .atMost(Duration.ofSeconds(30))
        .pollInterval(Duration.ofMillis(500))
        .untilAsserted(
            () -> {
              assertThat(attempts.get()).isGreaterThanOrEqualTo(1);
              assertThat(currentDlqDepth()).isGreaterThan(previousDlqDepth);
            });
  }

  @Test
  void socialEventFailureRoutesToDlq() {
    Mockito.doThrow(new RuntimeException("forced failure"))
        .when(dispatcher)
        .handle(Mockito.any(FriendRequestSent.class));

    int previousDlqDepth = currentDlqDepth();

    publisher.publish(new FriendRequestSent(1L, 2L));

    await()
        .atMost(Duration.ofSeconds(30))
        .pollInterval(Duration.ofMillis(500))
        .untilAsserted(() -> assertThat(currentDlqDepth()).isGreaterThan(previousDlqDepth));
  }

  @Test
  void userActionEventFailureRoutesToDlq() {
    Mockito.doThrow(new RuntimeException("forced failure"))
        .when(userActionHandler)
        .handle(Mockito.any(NotificationRead.class));

    int previousDlqDepth = currentDlqDepth();

    publisher.publish(new NotificationRead(1L, UUID.randomUUID()));

    await()
        .atMost(Duration.ofSeconds(30))
        .pollInterval(Duration.ofMillis(500))
        .untilAsserted(() -> assertThat(currentDlqDepth()).isGreaterThan(previousDlqDepth));
  }

  private int currentDlqDepth() {
    Properties props = amqpAdmin.getQueueProperties(EventRoutingKeys.DEAD_LETTER_QUEUE);
    if (props == null) {
      return 0;
    }
    Integer count = (Integer) props.get("QUEUE_MESSAGE_COUNT");
    return count == null ? 0 : count;
  }
}
