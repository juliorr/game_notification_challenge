package com.globalli.notifications.messaging;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.globalli.notifications.events.FriendRequestSent;
import com.globalli.notifications.events.NotificationRead;
import com.globalli.notifications.events.PlayerLeveledUp;
import com.globalli.notifications.service.NotificationDispatcher;
import com.globalli.notifications.service.UserActionHandler;
import com.rabbitmq.client.Channel;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationEventListenerTest {

  private NotificationDispatcher dispatcher;
  private UserActionHandler userActionHandler;
  private Channel channel;
  private NotificationEventListener listener;

  @BeforeEach
  void setUp() {
    dispatcher = mock(NotificationDispatcher.class);
    userActionHandler = mock(UserActionHandler.class);
    channel = mock(Channel.class);
    listener = new NotificationEventListener(dispatcher, userActionHandler);
  }

  @Test
  void onGameEventAcksOnSuccess() throws Exception {
    PlayerLeveledUp event = new PlayerLeveledUp(7L, 5);

    listener.onGameEvent(event, channel, 11L);

    verify(dispatcher).handle(event);
    verify(channel).basicAck(11L, false);
    verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
  }

  @Test
  void onGameEventNacksWithoutRequeueOnFailure() throws Exception {
    PlayerLeveledUp event = new PlayerLeveledUp(7L, 5);
    doThrow(new RuntimeException("boom")).when(dispatcher).handle(event);

    listener.onGameEvent(event, channel, 12L);

    verify(channel).basicNack(12L, false, false);
    verify(channel, never()).basicAck(anyLong(), anyBoolean());
  }

  @Test
  void onSocialEventAcksOnSuccess() throws Exception {
    FriendRequestSent event = new FriendRequestSent(1L, 2L);

    listener.onSocialEvent(event, channel, 21L);

    verify(dispatcher).handle(event);
    verify(channel).basicAck(21L, false);
    verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
  }

  @Test
  void onSocialEventNacksWithoutRequeueOnFailure() throws Exception {
    FriendRequestSent event = new FriendRequestSent(1L, 2L);
    doThrow(new RuntimeException("boom")).when(dispatcher).handle(event);

    listener.onSocialEvent(event, channel, 22L);

    verify(channel).basicNack(22L, false, false);
    verify(channel, never()).basicAck(anyLong(), anyBoolean());
  }

  @Test
  void onUserActionAcksOnSuccess() throws Exception {
    NotificationRead event = new NotificationRead(3L, UUID.randomUUID());

    listener.onUserAction(event, channel, 31L);

    verify(userActionHandler).handle(event);
    verify(channel).basicAck(31L, false);
    verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
  }

  @Test
  void onUserActionNacksWithoutRequeueOnFailure() throws Exception {
    NotificationRead event = new NotificationRead(3L, UUID.randomUUID());
    doThrow(new RuntimeException("boom")).when(userActionHandler).handle(event);

    listener.onUserAction(event, channel, 32L);

    verify(channel).basicNack(32L, false, false);
    verify(channel, never()).basicAck(anyLong(), anyBoolean());
  }
}
