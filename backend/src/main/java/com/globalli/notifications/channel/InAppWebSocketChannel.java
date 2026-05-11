package com.globalli.notifications.channel;

import com.globalli.notifications.domain.DeliveryChannel;
import com.globalli.notifications.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class InAppWebSocketChannel implements NotificationChannel {

  private static final Logger log = LoggerFactory.getLogger(InAppWebSocketChannel.class);

  private final SimpMessagingTemplate messagingTemplate;

  public InAppWebSocketChannel(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @Override
  public DeliveryChannel channel() {
    return DeliveryChannel.IN_APP;
  }

  @Override
  public void send(Notification notification) {
    String destination = WebSocketDestinations.forUser(notification.userId());
    log.info("Pushing notification {} to {}", notification.type(), destination);
    messagingTemplate.convertAndSend(destination, notification);
  }
}
