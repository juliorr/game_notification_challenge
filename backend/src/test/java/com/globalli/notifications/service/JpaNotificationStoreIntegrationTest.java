package com.globalli.notifications.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.domain.NotificationType;
import com.globalli.notifications.domain.persistence.NotificationEntityMapper;
import com.globalli.notifications.domain.persistence.NotificationJpaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaNotificationStore.class, NotificationEntityMapper.class})
@Testcontainers
class JpaNotificationStoreIntegrationTest {

  @SuppressWarnings("resource")
  static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
          .withDatabaseName("notifications")
          .withUsername("notif")
          .withPassword("notif");

  static {
    POSTGRES.start();
  }

  @DynamicPropertySource
  static void datasourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Autowired private JpaNotificationStore store;
  @Autowired private NotificationJpaRepository repository;

  @BeforeEach
  void clean() {
    repository.deleteAll();
  }

  @Test
  void appendAndFindByUserIdReturnsMostRecentFirst() {
    Notification first = Notification.create(1L, NotificationType.LEVEL_UP, "first");
    sleep();
    Notification second = Notification.create(1L, NotificationType.LEVEL_UP, "second");

    store.append(first);
    store.append(second);

    List<Notification> result = store.findByUserId(1L);
    assertThat(result).extracting(Notification::message).containsExactly("second", "first");
  }

  @Test
  void findPageRespectsCursor() {
    Notification first = Notification.create(1L, NotificationType.LEVEL_UP, "first");
    sleep();
    Notification second = Notification.create(1L, NotificationType.LEVEL_UP, "second");
    sleep();
    Notification third = Notification.create(1L, NotificationType.LEVEL_UP, "third");
    store.append(first);
    store.append(second);
    store.append(third);

    List<Notification> page = store.findPage(1L, Optional.of(third.timestamp()), 10);

    assertThat(page).extracting(Notification::message).containsExactly("second", "first");
  }

  @Test
  void unreadCountAndMarkAllRead() {
    store.append(Notification.create(1L, NotificationType.LEVEL_UP, "a"));
    store.append(Notification.create(1L, NotificationType.LEVEL_UP, "b"));
    store.append(Notification.create(2L, NotificationType.LEVEL_UP, "other"));

    assertThat(store.unreadCount(1L)).isEqualTo(2);

    int updated = store.markAllRead(1L);
    assertThat(updated).isEqualTo(2);
    assertThat(store.unreadCount(1L)).isZero();
    assertThat(store.unreadCount(2L)).isEqualTo(1);
    assertThat(store.findByUserId(1L)).allSatisfy(n -> assertThat(n.readAt()).isNotNull());
  }

  private static void sleep() {
    try {
      Thread.sleep(5);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
