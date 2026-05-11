package com.globalli.notifications.simulators;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.globalli.notifications.events.ChallengeCompleted;
import com.globalli.notifications.events.ItemAcquired;
import com.globalli.notifications.events.PlayerAttackedInPvp;
import com.globalli.notifications.events.PlayerDefeatedInPvp;
import com.globalli.notifications.events.PlayerLeveledUp;
import com.globalli.notifications.messaging.DomainEventPublisher;
import com.globalli.notifications.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GameEngineTest {

  @Mock private DomainEventPublisher publisher;
  @Mock private UserProfileService userProfileService;

  private GameEngine engine;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    engine = new GameEngine(publisher, userProfileService);
  }

  @Test
  void publishesPlayerLeveledUpWithIncrementedLevel() {
    when(userProfileService.incrementLevel(7L)).thenReturn(5);

    engine.playerLeveledUp(7L);

    verify(publisher).publish(new PlayerLeveledUp(7L, 5));
  }

  @Test
  void publishesItemAcquired() {
    engine.itemAcquired(1L, "Sword");

    verify(publisher).publish(new ItemAcquired(1L, "Sword"));
  }

  @Test
  void publishesChallengeCompleted() {
    engine.challengeCompleted(2L, "Daily");

    verify(publisher).publish(new ChallengeCompleted(2L, "Daily"));
  }

  @Test
  void publishesPlayerDefeatedInPvp() {
    engine.playerDefeatedInPvp(3L, 9L);

    verify(publisher).publish(new PlayerDefeatedInPvp(3L, 9L));
  }

  @Test
  void publishesPlayerAttackedInPvp() {
    engine.playerAttackedInPvp(3L, 9L);

    verify(publisher).publish(new PlayerAttackedInPvp(3L, 9L));
  }
}
