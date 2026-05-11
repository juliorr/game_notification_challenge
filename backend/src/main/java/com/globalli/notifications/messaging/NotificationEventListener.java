package com.globalli.notifications.messaging;

import com.globalli.notifications.events.GameEvent;
import com.globalli.notifications.events.SocialEvent;
import com.globalli.notifications.events.UserActionEvent;
import com.globalli.notifications.service.NotificationDispatcher;
import com.globalli.notifications.service.UserActionHandler;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

  private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

  private final NotificationDispatcher dispatcher;
  private final UserActionHandler userActionHandler;

  public NotificationEventListener(
      NotificationDispatcher dispatcher, UserActionHandler userActionHandler) {
    this.dispatcher = dispatcher;
    this.userActionHandler = userActionHandler;
  }

  @RabbitListener(queues = EventRoutingKeys.GAME_QUEUE)
  public void onGameEvent(
      GameEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag)
      throws IOException {
    handle(event, () -> dispatcher.handle(event), channel, deliveryTag);
  }

  @RabbitListener(queues = EventRoutingKeys.SOCIAL_QUEUE)
  public void onSocialEvent(
      SocialEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag)
      throws IOException {
    handle(event, () -> dispatcher.handle(event), channel, deliveryTag);
  }

  @RabbitListener(queues = EventRoutingKeys.USER_QUEUE)
  public void onUserAction(
      UserActionEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag)
      throws IOException {
    handle(event, () -> userActionHandler.handle(event), channel, deliveryTag);
  }

  private void handle(Object event, Runnable action, Channel channel, long deliveryTag)
      throws IOException {
    try {
      action.run();
      channel.basicAck(deliveryTag, false);
    } catch (RuntimeException ex) {
      log.error(
          "Listener failed for event {} (deliveryTag={}); nacking for redelivery,"
              + " delivery-limit will route to DLX",
          event.getClass().getSimpleName(),
          deliveryTag,
          ex);
      channel.basicNack(deliveryTag, false, false);
    }
  }
}
