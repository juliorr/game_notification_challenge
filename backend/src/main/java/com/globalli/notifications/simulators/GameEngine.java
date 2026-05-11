package com.globalli.notifications.simulators;

import com.globalli.notifications.events.ChallengeCompleted;
import com.globalli.notifications.events.ItemAcquired;
import com.globalli.notifications.events.PlayerAttackedInPvp;
import com.globalli.notifications.events.PlayerDefeatedInPvp;
import com.globalli.notifications.events.PlayerLeveledUp;
import com.globalli.notifications.messaging.DomainEventPublisher;
import com.globalli.notifications.service.UserProfileService;
import org.springframework.stereotype.Component;

@Component
public class GameEngine {

  private final DomainEventPublisher eventPublisher;
  private final UserProfileService userProfileService;

  public GameEngine(DomainEventPublisher eventPublisher, UserProfileService userProfileService) {
    this.eventPublisher = eventPublisher;
    this.userProfileService = userProfileService;
  }

  public void playerLeveledUp(long userId) {
    int newLevel = userProfileService.incrementLevel(userId);
    eventPublisher.publish(new PlayerLeveledUp(userId, newLevel));
  }

  public void itemAcquired(long userId, String itemName) {
    eventPublisher.publish(new ItemAcquired(userId, itemName));
  }

  public void challengeCompleted(long userId, String challengeName) {
    eventPublisher.publish(new ChallengeCompleted(userId, challengeName));
  }

  public void playerDefeatedInPvp(long userId, long attackerUserId) {
    eventPublisher.publish(new PlayerDefeatedInPvp(userId, attackerUserId));
  }

  public void playerAttackedInPvp(long userId, long attackerUserId) {
    eventPublisher.publish(new PlayerAttackedInPvp(userId, attackerUserId));
  }
}
