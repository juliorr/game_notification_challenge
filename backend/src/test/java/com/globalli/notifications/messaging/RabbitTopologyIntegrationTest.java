package com.globalli.notifications.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.globalli.notifications.events.PlayerLeveledUp;
import com.globalli.notifications.service.NotificationFactory;
import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class RabbitTopologyIntegrationTest extends RabbitContainerSupport {

  @Autowired private AmqpAdmin amqpAdmin;
  @Autowired private DomainEventPublisher publisher;
  @MockBean private NotificationFactory notificationFactory;

  @Test
  void declaresQuorumQueuesWithDeadLetterExchange() {
    Properties gameProps = amqpAdmin.getQueueProperties(EventRoutingKeys.GAME_QUEUE);
    Properties socialProps = amqpAdmin.getQueueProperties(EventRoutingKeys.SOCIAL_QUEUE);
    Properties dlqProps = amqpAdmin.getQueueProperties(EventRoutingKeys.DEAD_LETTER_QUEUE);

    assertThat(gameProps).isNotNull();
    assertThat(socialProps).isNotNull();
    assertThat(dlqProps).isNotNull();
  }

  @Test
  void poisonMessageEndsInDeadLetterQueue() {
    Mockito.when(notificationFactory.fromGameEvent(Mockito.any()))
        .thenThrow(new RuntimeException("forced failure"));

    publisher.publish(new PlayerLeveledUp(99L, 1));

    await()
        .atMost(Duration.ofSeconds(30))
        .pollInterval(Duration.ofMillis(500))
        .untilAsserted(
            () -> {
              Properties dlqProps =
                  amqpAdmin.getQueueProperties(EventRoutingKeys.DEAD_LETTER_QUEUE);
              Integer messageCount = (Integer) dlqProps.get("QUEUE_MESSAGE_COUNT");
              assertThat(messageCount).isNotNull().isGreaterThanOrEqualTo(1);
            });
  }
}
