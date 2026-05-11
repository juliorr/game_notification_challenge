package com.globalli.notifications.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.globalli.notifications.channel.ReadStatusChannel;
import com.globalli.notifications.events.AllNotificationsRead;
import com.globalli.notifications.events.NotificationRead;
import com.globalli.notifications.events.NotificationsCleared;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserActionHandlerTest {

  @Mock private NotificationStore store;
  @Mock private ReadStatusChannel channel;

  private UserActionHandler handler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    handler = new UserActionHandler(store, channel);
  }

  @Test
  void notificationReadUpdatesStoreAndBroadcasts() {
    UUID id = UUID.randomUUID();
    when(store.markRead(7L, id)).thenReturn(true);

    handler.handle(new NotificationRead(7L, id));

    verify(store).markRead(7L, id);
    verify(channel).broadcastSingle(eq(7L), eq(id), any(Instant.class));
  }

  @Test
  void notificationReadSkipsBroadcastWhenAlreadyReadOrMissing() {
    UUID id = UUID.randomUUID();
    when(store.markRead(7L, id)).thenReturn(false);

    handler.handle(new NotificationRead(7L, id));

    verify(channel, never()).broadcastSingle(anyLong(), any(UUID.class), any(Instant.class));
  }

  @Test
  void allNotificationsReadUpdatesStoreAndBroadcasts() {
    when(store.markAllRead(7L)).thenReturn(3);

    handler.handle(new AllNotificationsRead(7L));

    verify(store).markAllRead(7L);
    verify(channel).broadcastAll(eq(7L), any(Instant.class));
  }

  @Test
  void allNotificationsReadSkipsBroadcastWhenNothingUnread() {
    when(store.markAllRead(7L)).thenReturn(0);

    handler.handle(new AllNotificationsRead(7L));

    verify(channel, never()).broadcastAll(anyLong(), any(Instant.class));
  }

  @Test
  void notificationsClearedDeletesAndBroadcasts() {
    when(store.clearAll(7L)).thenReturn(4);

    handler.handle(new NotificationsCleared(7L));

    verify(store).clearAll(7L);
    verify(channel).broadcastCleared(eq(7L), any(Instant.class));
  }

  @Test
  void notificationsClearedSkipsBroadcastWhenNothingToClear() {
    when(store.clearAll(7L)).thenReturn(0);

    handler.handle(new NotificationsCleared(7L));

    verify(channel, never()).broadcastCleared(anyLong(), any(Instant.class));
  }
}
