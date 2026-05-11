package com.globalli.notifications.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.globalli.notifications.channel.NotificationChannel;
import com.globalli.notifications.domain.DeliveryChannel;
import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.domain.NotificationCategory;
import com.globalli.notifications.events.FriendRequestSent;
import com.globalli.notifications.events.PlayerLeveledUp;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class NotificationDispatcherTest {

  @Mock private NotificationChannel inAppChannel;
  @Mock private NotificationChannel emailChannel;
  @Mock private NotificationStore store;

  private NotificationFactory factory;
  private UserPreferenceService preferences;
  private NotificationDispatcher dispatcher;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(inAppChannel.channel()).thenReturn(DeliveryChannel.IN_APP);
    when(emailChannel.channel()).thenReturn(DeliveryChannel.EMAIL);
    factory = new NotificationFactory(new NotificationTemplateService());
    preferences = new UserPreferenceService();
    dispatcher =
        new NotificationDispatcher(
            factory, preferences, store, List.of(inAppChannel, emailChannel));
  }

  @Test
  void deliversGameEventToAllChannelsWhenCategoryEnabled() {
    dispatcher.handle(new PlayerLeveledUp(1L, 15));

    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    verify(inAppChannel).send(captor.capture());
    verify(emailChannel).send(captor.capture());
  }

  @Test
  void doesNotDeliverGameEventWhenGameCategoryDisabled() {
    preferences.setCategoryEnabled(1L, NotificationCategory.GAME, false);

    dispatcher.handle(new PlayerLeveledUp(1L, 15));

    verify(inAppChannel, never()).send(any());
    verify(emailChannel, never()).send(any());
  }

  @Test
  void deliversSocialEventEvenWhenGameDisabled() {
    preferences.setCategoryEnabled(1L, NotificationCategory.GAME, false);

    dispatcher.handle(new FriendRequestSent(3L, 1L));

    verify(inAppChannel, times(1)).send(any());
    verify(emailChannel, times(1)).send(any());
  }

  @Test
  void doesNotDeliverSocialEventWhenSocialDisabled() {
    preferences.setCategoryEnabled(1L, NotificationCategory.SOCIAL, false);

    dispatcher.handle(new FriendRequestSent(3L, 1L));

    verify(inAppChannel, never()).send(any());
  }

  @Test
  void masterSwitchOffSkipsPersistAndAllChannels() {
    preferences.setNotificationsEnabled(1L, false);

    dispatcher.handle(new PlayerLeveledUp(1L, 15));

    verify(inAppChannel, never()).send(any());
    verify(emailChannel, never()).send(any());
    verify(store, never()).append(any());
  }

  @Test
  void deliversOnlyToEnabledChannel() {
    preferences.setChannelEnabled(1L, DeliveryChannel.IN_APP, false);

    dispatcher.handle(new FriendRequestSent(3L, 1L));

    verify(inAppChannel, never()).send(any());
    verify(emailChannel, times(1)).send(any());
    verify(store, times(1)).append(any());
  }

  @Test
  void disablingEmailKeepsInAppDelivery() {
    preferences.setChannelEnabled(1L, DeliveryChannel.EMAIL, false);

    dispatcher.handle(new PlayerLeveledUp(1L, 7));

    verify(inAppChannel, times(1)).send(any());
    verify(emailChannel, never()).send(any());
  }
}
