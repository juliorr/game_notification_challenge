package com.globalli.notifications.messaging;

import com.globalli.notifications.events.GameEvent;
import com.globalli.notifications.events.SocialEvent;
import com.globalli.notifications.events.UserActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitDomainEventPublisher implements DomainEventPublisher {

  private static final Logger log = LoggerFactory.getLogger(RabbitDomainEventPublisher.class);

  private final RabbitTemplate rabbitTemplate;

  public RabbitDomainEventPublisher(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void publish(GameEvent event) {
    doPublish(event, EventRoutingKeys.routingKeyFor(event));
  }

  @Override
  public void publish(SocialEvent event) {
    doPublish(event, EventRoutingKeys.routingKeyFor(event));
  }

  @Override
  public void publish(UserActionEvent event) {
    doPublish(event, EventRoutingKeys.routingKeyFor(event));
  }

  private void doPublish(Object event, String routingKey) {
    try {
      rabbitTemplate.convertAndSend(EventRoutingKeys.EVENTS_EXCHANGE, routingKey, event);
    } catch (AmqpException ex) {
      log.error(
          "Failed to publish event {} with routing key {}",
          event.getClass().getSimpleName(),
          routingKey,
          ex);
      throw new MessagePublishException(
          "Failed to publish " + event.getClass().getSimpleName() + " to " + routingKey, ex);
    }
  }
}
