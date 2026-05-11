package com.globalli.notifications.channel;

import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusChannel {

  private static final Logger log = LoggerFactory.getLogger(ReadStatusChannel.class);

  private final SimpMessagingTemplate messagingTemplate;

  public ReadStatusChannel(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  public void broadcastSingle(long userId, UUID notificationId, Instant readAt) {
    String destination = WebSocketDestinations.readForUser(userId);
    ReadStatusPayload payload = new ReadStatusPayload(notificationId, readAt);
    log.info("Pushing read status {} to {}", payload, destination);
    messagingTemplate.convertAndSend(destination, payload);
  }

  public void broadcastAll(long userId, Instant readAt) {
    String destination = WebSocketDestinations.readAllForUser(userId);
    ReadAllStatusPayload payload = new ReadAllStatusPayload(readAt);
    log.info("Pushing read-all status {} to {}", payload, destination);
    messagingTemplate.convertAndSend(destination, payload);
  }

  public void broadcastCleared(long userId, Instant clearedAt) {
    String destination = WebSocketDestinations.clearedForUser(userId);
    ClearedPayload payload = new ClearedPayload(clearedAt);
    log.info("Pushing cleared status {} to {}", payload, destination);
    messagingTemplate.convertAndSend(destination, payload);
  }

  public record ReadStatusPayload(UUID notificationId, Instant readAt) {}

  public record ReadAllStatusPayload(Instant readAt) {}

  public record ClearedPayload(Instant clearedAt) {}
}
