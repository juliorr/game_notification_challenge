package com.globalli.notifications.channel;

import static org.mockito.Mockito.verify;

import com.globalli.notifications.channel.ReadStatusChannel.ClearedPayload;
import com.globalli.notifications.channel.ReadStatusChannel.ReadAllStatusPayload;
import com.globalli.notifications.channel.ReadStatusChannel.ReadStatusPayload;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

class ReadStatusChannelTest {

  @Mock private SimpMessagingTemplate messagingTemplate;

  private ReadStatusChannel channel;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    channel = new ReadStatusChannel(messagingTemplate);
  }

  @Test
  void broadcastSingleSendsReadStatus() {
    UUID id = UUID.randomUUID();
    Instant readAt = Instant.parse("2026-05-09T00:00:00Z");

    channel.broadcastSingle(7L, id, readAt);

    verify(messagingTemplate)
        .convertAndSend("/topic/notifications/7/read", new ReadStatusPayload(id, readAt));
  }

  @Test
  void broadcastAllSendsAllReadStatus() {
    Instant readAt = Instant.parse("2026-05-09T00:00:00Z");

    channel.broadcastAll(7L, readAt);

    verify(messagingTemplate)
        .convertAndSend("/topic/notifications/7/read-all", new ReadAllStatusPayload(readAt));
  }

  @Test
  void broadcastClearedSendsClearedPayload() {
    Instant clearedAt = Instant.parse("2026-05-09T00:00:00Z");

    channel.broadcastCleared(7L, clearedAt);

    verify(messagingTemplate)
        .convertAndSend("/topic/notifications/7/cleared", new ClearedPayload(clearedAt));
  }
}
