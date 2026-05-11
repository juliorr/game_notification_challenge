package com.globalli.notifications.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.globalli.notifications.email.MockEmail;
import com.globalli.notifications.email.MockEmailStore;
import com.globalli.notifications.messaging.RabbitContainerSupport;
import com.globalli.notifications.simulators.GameEngine;
import com.globalli.notifications.simulators.SocialSystem;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EmailMockChannelIntegrationTest extends RabbitContainerSupport {

  @Autowired private SocialSystem socialSystem;
  @Autowired private GameEngine gameEngine;
  @Autowired private MockEmailStore mockEmailStore;

  @BeforeEach
  void resetMailbox() {
    mockEmailStore.clear();
  }

  @Test
  void friendRequestProducesMockEmail() {
    socialSystem.friendRequestSent(7L, 42L);

    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () -> {
              var emails = mockEmailStore.findByUserId(42L);
              assertThat(emails).hasSize(1);
              MockEmail email = emails.get(0);
              assertThat(email.subject()).isEqualTo("You have a new friend request");
              assertThat(email.to()).isEqualTo("user-42@globalli.local");
            });
  }

  @Test
  void friendAcceptedProducesMockEmail() {
    socialSystem.friendRequestAccepted(3L, 99L);

    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () -> {
              var emails = mockEmailStore.findByUserId(99L);
              assertThat(emails).hasSize(1);
              assertThat(emails.get(0).subject()).isEqualTo("Your friend request was accepted");
            });
  }

  @Test
  void gameEventDoesNotProduceMockEmail() throws InterruptedException {
    gameEngine.playerLeveledUp(5L);

    Thread.sleep(1500);
    assertThat(mockEmailStore.findAll()).isEmpty();
  }
}
