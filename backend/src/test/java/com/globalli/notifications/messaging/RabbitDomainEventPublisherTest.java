package com.globalli.notifications.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.globalli.notifications.events.FriendRequestSent;
import com.globalli.notifications.events.NotificationRead;
import com.globalli.notifications.events.PlayerLeveledUp;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

class RabbitDomainEventPublisherTest {

  private RabbitTemplate rabbitTemplate;
  private RabbitDomainEventPublisher publisher;

  @BeforeEach
  void setUp() {
    rabbitTemplate = mock(RabbitTemplate.class);
    publisher = new RabbitDomainEventPublisher(rabbitTemplate);
  }

  @Test
  void publishGameEventSendsToExchangeWithRoutingKey() {
    PlayerLeveledUp event = new PlayerLeveledUp(1L, 2);

    publisher.publish(event);

    verify(rabbitTemplate)
        .convertAndSend(
            EventRoutingKeys.EVENTS_EXCHANGE, EventRoutingKeys.GAME_PLAYER_LEVELED_UP, event);
  }

  @Test
  void publishSocialEventSendsToExchangeWithRoutingKey() {
    FriendRequestSent event = new FriendRequestSent(1L, 2L);

    publisher.publish(event);

    verify(rabbitTemplate)
        .convertAndSend(
            EventRoutingKeys.EVENTS_EXCHANGE, EventRoutingKeys.SOCIAL_FRIEND_REQUEST_SENT, event);
  }

  @Test
  void publishUserActionEventSendsToExchangeWithRoutingKey() {
    NotificationRead event = new NotificationRead(1L, UUID.randomUUID());

    publisher.publish(event);

    verify(rabbitTemplate)
        .convertAndSend(
            EventRoutingKeys.EVENTS_EXCHANGE, EventRoutingKeys.USER_NOTIFICATION_READ, event);
  }

  @Test
  void wrapsAmqpExceptionAsMessagePublishExceptionWithRoutingKeyInMessage() {
    PlayerLeveledUp event = new PlayerLeveledUp(1L, 2);
    AmqpException cause = new AmqpException("broker down");
    doThrow(cause)
        .when(rabbitTemplate)
        .convertAndSend(
            EventRoutingKeys.EVENTS_EXCHANGE, EventRoutingKeys.GAME_PLAYER_LEVELED_UP, event);

    assertThatThrownBy(() -> publisher.publish(event))
        .isInstanceOf(MessagePublishException.class)
        .hasCause(cause)
        .hasMessageContaining(EventRoutingKeys.GAME_PLAYER_LEVELED_UP)
        .hasMessageContaining("PlayerLeveledUp");
  }

  @Test
  void wrapsAmqpExceptionForSocialEvents() {
    FriendRequestSent event = new FriendRequestSent(1L, 2L);
    doThrow(new AmqpException("broker down"))
        .when(rabbitTemplate)
        .convertAndSend(
            EventRoutingKeys.EVENTS_EXCHANGE, EventRoutingKeys.SOCIAL_FRIEND_REQUEST_SENT, event);

    assertThatThrownBy(() -> publisher.publish(event))
        .isInstanceOf(MessagePublishException.class)
        .satisfies(
            ex ->
                assertThat(ex.getMessage()).contains(EventRoutingKeys.SOCIAL_FRIEND_REQUEST_SENT));
  }
}
