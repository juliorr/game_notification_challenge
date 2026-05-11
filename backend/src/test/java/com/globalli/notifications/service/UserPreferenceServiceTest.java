package com.globalli.notifications.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.globalli.notifications.domain.DeliveryChannel;
import com.globalli.notifications.domain.NotificationCategory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class UserPreferenceServiceTest {

  private final UserPreferenceService service = new UserPreferenceService();

  @Test
  void enablesAllCategoriesByDefault() {
    assertThat(service.isCategoryEnabled(1L, NotificationCategory.GAME)).isTrue();
    assertThat(service.isCategoryEnabled(1L, NotificationCategory.SOCIAL)).isTrue();
  }

  @Test
  void disablingGameLeavesSocialEnabled() {
    service.setCategoryEnabled(1L, NotificationCategory.GAME, false);

    assertThat(service.isCategoryEnabled(1L, NotificationCategory.GAME)).isFalse();
    assertThat(service.isCategoryEnabled(1L, NotificationCategory.SOCIAL)).isTrue();
  }

  @Test
  void preferencesAreScopedPerUser() {
    service.setCategoryEnabled(1L, NotificationCategory.GAME, false);

    assertThat(service.isCategoryEnabled(2L, NotificationCategory.GAME)).isTrue();
  }

  @Test
  void reEnablingRestoresAccess() {
    service.setCategoryEnabled(1L, NotificationCategory.SOCIAL, false);
    service.setCategoryEnabled(1L, NotificationCategory.SOCIAL, true);

    assertThat(service.isCategoryEnabled(1L, NotificationCategory.SOCIAL)).isTrue();
  }

  @Test
  void notificationsAreEnabledByDefault() {
    assertThat(service.isNotificationsEnabled(1L)).isTrue();
  }

  @Test
  void masterSwitchTogglesPerUser() {
    service.setNotificationsEnabled(1L, false);

    assertThat(service.isNotificationsEnabled(1L)).isFalse();
    assertThat(service.isNotificationsEnabled(2L)).isTrue();
  }

  @Test
  void allChannelsEnabledByDefault() {
    assertThat(service.isChannelEnabled(1L, DeliveryChannel.IN_APP)).isTrue();
    assertThat(service.isChannelEnabled(1L, DeliveryChannel.EMAIL)).isTrue();
  }

  @Test
  void disablingEmailLeavesInAppEnabled() {
    service.setChannelEnabled(1L, DeliveryChannel.EMAIL, false);

    assertThat(service.isChannelEnabled(1L, DeliveryChannel.IN_APP)).isTrue();
    assertThat(service.isChannelEnabled(1L, DeliveryChannel.EMAIL)).isFalse();
  }

  @Test
  void channelsAreScopedPerUser() {
    service.setChannelEnabled(1L, DeliveryChannel.IN_APP, false);

    assertThat(service.isChannelEnabled(2L, DeliveryChannel.IN_APP)).isTrue();
  }

  @Test
  void concurrentReadsAndWritesDoNotThrow() throws InterruptedException {
    long userId = 99L;
    int writers = 4;
    int readers = 4;
    int iterationsPerThread = 500;
    ExecutorService executor = Executors.newFixedThreadPool(writers + readers);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(writers + readers);
    AtomicBoolean failed = new AtomicBoolean(false);

    NotificationCategory[] categories = NotificationCategory.values();

    for (int w = 0; w < writers; w++) {
      executor.submit(
          () -> {
            try {
              start.await();
              for (int i = 0; i < iterationsPerThread; i++) {
                NotificationCategory category = categories[i % categories.length];
                service.setCategoryEnabled(userId, category, (i & 1) == 0);
              }
            } catch (Exception ex) {
              failed.set(true);
            } finally {
              done.countDown();
            }
          });
    }

    for (int r = 0; r < readers; r++) {
      executor.submit(
          () -> {
            try {
              start.await();
              for (int i = 0; i < iterationsPerThread; i++) {
                service.getEnabledCategories(userId);
                service.isCategoryEnabled(userId, categories[i % categories.length]);
              }
            } catch (Exception ex) {
              failed.set(true);
            } finally {
              done.countDown();
            }
          });
    }

    start.countDown();
    assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();
    executor.shutdownNow();
    assertThat(failed).isFalse();
  }
}
