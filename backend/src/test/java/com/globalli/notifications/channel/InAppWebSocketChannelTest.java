package com.globalli.notifications.channel;

import static org.mockito.Mockito.verify;

import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.domain.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

class InAppWebSocketChannelTest {

  @Mock private SimpMessagingTemplate messagingTemplate;

  private InAppWebSocketChannel channel;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    channel = new InAppWebSocketChannel(messagingTemplate);
  }

  @Test
  void sendsNotificationToUserDestination() {
    Notification notification = Notification.create(42L, NotificationType.LEVEL_UP, "Up!");

    channel.send(notification);

    verify(messagingTemplate).convertAndSend("/topic/notifications/42", notification);
  }
}
